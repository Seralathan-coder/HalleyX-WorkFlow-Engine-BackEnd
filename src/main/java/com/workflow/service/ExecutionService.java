package com.workflow.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.workflow.dto.ExecutionDTO;
import com.workflow.model.Execution;
import com.workflow.model.Step;
import com.workflow.model.Workflow;
import com.workflow.repository.ExecutionRepository;
import com.workflow.repository.StepRepository;
import com.workflow.repository.WorkflowRepository;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class ExecutionService {

    private static final Logger log = LoggerFactory.getLogger(ExecutionService.class);
    private static final int MAX_ITERATIONS = 100;

    private final ExecutionRepository executionRepository;
    private final WorkflowRepository workflowRepository;
    private final StepRepository stepRepository;
    private final RuleEngineService ruleEngineService;
    private final NotificationService notificationService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public ExecutionService(ExecutionRepository executionRepository,
                            WorkflowRepository workflowRepository,
                            StepRepository stepRepository,
                            RuleEngineService ruleEngineService,
                            NotificationService notificationService) {
        this.executionRepository = executionRepository;
        this.workflowRepository = workflowRepository;
        this.stepRepository = stepRepository;
        this.ruleEngineService = ruleEngineService;
        this.notificationService = notificationService;
    }

    @Transactional
    public ExecutionDTO startExecution(String workflowId, String dataJson, String triggeredBy) {
        Workflow workflow = workflowRepository.findById(workflowId)
                .orElseThrow(() -> new EntityNotFoundException("Workflow not found: " + workflowId));

        Execution execution = Execution.builder()
                .id(UUID.randomUUID().toString())
                .workflowId(workflowId)
                .workflowVersion(workflow.getVersion())
                .status(Execution.ExecutionStatus.in_progress)
                .data(dataJson)
                .logs("[]")
                .retries(0)
                .triggeredBy(triggeredBy != null ? triggeredBy : "system")
                .startedAt(LocalDateTime.now())
                .currentStepId(workflow.getStartStepId())
                .build();
        execution = executionRepository.save(execution);

        if (workflow.getStartStepId() == null) {
            appendLog(execution, "No start step configured — workflow completed immediately");
            execution.setStatus(Execution.ExecutionStatus.completed);
            execution.setEndedAt(LocalDateTime.now());
            return toDTO(executionRepository.save(execution));
        }

        execution = runExecution(execution);
        return toDTO(executionRepository.save(execution));
    }

    private Execution runExecution(Execution execution) {
        int iteration = 0;
        String currentStepId = execution.getCurrentStepId();

        while (currentStepId != null && iteration < MAX_ITERATIONS) {
            iteration++;
            Step step = stepRepository.findById(currentStepId).orElse(null);
            if (step == null) {
                appendLog(execution, "Step not found: " + currentStepId);
                execution.setStatus(Execution.ExecutionStatus.failed);
                execution.setEndedAt(LocalDateTime.now());
                return execution;
            }

            execution.setCurrentStepId(currentStepId);
            appendLog(execution, "Executing step [" + step.getStepType() + "]: " + step.getName());

            try {
                String result = executeStep(step, execution);
                appendLog(execution, "Step completed: " + step.getName() + " → " + result);

                if (step.getStepType() == Step.StepType.approval) {
                    appendLog(execution, "Waiting for approval at step: " + step.getName());
                    execution.setStatus(Execution.ExecutionStatus.in_progress);
                    return executionRepository.save(execution);
                }

                RuleEngineService.RuleEvaluationResult ruleResult =
                        ruleEngineService.evaluateRules(currentStepId, execution.getData());
                appendLog(execution, "Rule evaluation: " + ruleResult.reason());
                currentStepId = ruleResult.nextStepId();
                execution.setCurrentStepId(currentStepId);
                execution = executionRepository.save(execution);

            } catch (Exception e) {
                log.error("Error executing step {}: {}", step.getName(), e.getMessage());
                appendLog(execution, "ERROR in step " + step.getName() + ": " + e.getMessage());
                execution.setStatus(Execution.ExecutionStatus.failed);
                execution.setEndedAt(LocalDateTime.now());
                return execution;
            }
        }

        if (iteration >= MAX_ITERATIONS) {
            appendLog(execution, "Max iterations reached — possible infinite loop detected");
            execution.setStatus(Execution.ExecutionStatus.failed);
        } else {
            appendLog(execution, "Workflow completed successfully");
            execution.setStatus(Execution.ExecutionStatus.completed);
        }
        execution.setEndedAt(LocalDateTime.now());
        return execution;
    }

    private String executeStep(Step step, Execution execution) {
        Map<String, Object> metadata = parseJson(step.getMetadata());
        return switch (step.getStepType()) {
            case task -> {
                String action = metadata != null ? (String) metadata.get("action") : "unknown";
                log.info("[TASK] Execution={} Step={} Action={}", execution.getId(), step.getName(), action);
                yield "Task executed: " + action;
            }
            case approval -> {
                String assignee = metadata != null ? (String) metadata.get("assignee") : "approver";
                log.info("[APPROVAL] Execution={} Step={} Assignee={}", execution.getId(), step.getName(), assignee);
                yield "Awaiting approval from: " + assignee;
            }
            case notification -> {
                String channel = metadata != null ? (String) metadata.get("channel") : "email";
                String assignee = metadata != null ? (String) metadata.get("assignee") : null;
                String message = metadata != null ? (String) metadata.get("message") : "Workflow notification";
                yield notificationService.sendNotification(channel, assignee, message, execution.getId());
            }
        };
    }

    @Transactional
    public ExecutionDTO approveStep(String executionId, boolean approved) {
        Execution execution = executionRepository.findById(executionId)
                .orElseThrow(() -> new EntityNotFoundException("Execution not found: " + executionId));
        if (execution.getStatus() != Execution.ExecutionStatus.in_progress) {
            throw new IllegalStateException("Execution is not in_progress");
        }
        String decision = approved ? "APPROVED" : "REJECTED";
        appendLog(execution, "Step " + decision + " at: " + LocalDateTime.now());
        if (!approved) {
            execution.setData(addRejectionToData(execution.getData()));
        }

        RuleEngineService.RuleEvaluationResult ruleResult =
                ruleEngineService.evaluateRules(execution.getCurrentStepId(), execution.getData());
        appendLog(execution, "Post-approval rule: " + ruleResult.reason());
        execution.setCurrentStepId(ruleResult.nextStepId());
        execution = executionRepository.save(execution);

        if (ruleResult.nextStepId() != null) {
            execution = runExecution(execution);
        } else {
            execution.setStatus(Execution.ExecutionStatus.completed);
            execution.setEndedAt(LocalDateTime.now());
        }
        return toDTO(executionRepository.save(execution));
    }

    @Transactional
    public ExecutionDTO cancelExecution(String executionId) {
        Execution execution = executionRepository.findById(executionId)
                .orElseThrow(() -> new EntityNotFoundException("Execution not found: " + executionId));
        execution.setStatus(Execution.ExecutionStatus.canceled);
        execution.setEndedAt(LocalDateTime.now());
        appendLog(execution, "Execution canceled at: " + LocalDateTime.now());
        return toDTO(executionRepository.save(execution));
    }

    @Transactional
    public ExecutionDTO retryExecution(String executionId) {
        Execution execution = executionRepository.findById(executionId)
                .orElseThrow(() -> new EntityNotFoundException("Execution not found: " + executionId));
        if (execution.getStatus() != Execution.ExecutionStatus.failed) {
            throw new IllegalStateException("Can only retry failed executions");
        }
        execution.setRetries(execution.getRetries() + 1);
        execution.setStatus(Execution.ExecutionStatus.in_progress);
        appendLog(execution, "Retrying execution (attempt " + execution.getRetries() + ")");
        execution = runExecution(execution);
        return toDTO(executionRepository.save(execution));
    }

    public ExecutionDTO getExecution(String executionId) {
        Execution execution = executionRepository.findById(executionId)
                .orElseThrow(() -> new EntityNotFoundException("Execution not found: " + executionId));
        return toDTO(execution);
    }

    public Page<ExecutionDTO> listAllExecutions(Pageable pageable) {
        return executionRepository.findAllByOrderByCreatedAtDesc(pageable).map(this::toDTO);
    }

    public List<ExecutionDTO> listExecutionsByWorkflow(String workflowId) {
        return executionRepository.findByWorkflowIdOrderByCreatedAtDesc(workflowId)
                .stream().map(this::toDTO).toList();
    }

    // ---- Helpers ----

    private void appendLog(Execution execution, String message) {
        String timestampedMsg = "[" + LocalDateTime.now() + "] " + message;
        log.debug("[Execution {}] {}", execution.getId(), message);
        try {
            List<String> logs = parseLogs(execution.getLogs());
            logs.add(timestampedMsg);
            execution.setLogs(objectMapper.writeValueAsString(logs));
        } catch (Exception e) {
            log.error("Failed to append log: {}", e.getMessage());
        }
    }

    private List<String> parseLogs(String logsJson) {
        if (logsJson == null || logsJson.isBlank()) return new ArrayList<>();
        try { return objectMapper.readValue(logsJson, new TypeReference<List<String>>() {}); }
        catch (Exception e) { return new ArrayList<>(); }
    }

    private Map<String, Object> parseJson(String json) {
        if (json == null || json.isBlank()) return null;
        try { return objectMapper.readValue(json, new TypeReference<Map<String, Object>>() {}); }
        catch (Exception e) { return null; }
    }

    private String addRejectionToData(String dataJson) {
        try {
            Map<String, Object> data = parseJson(dataJson);
            if (data == null) data = new HashMap<>();
            data.put("rejected", true);
            return objectMapper.writeValueAsString(data);
        } catch (Exception e) { return dataJson; }
    }

    public ExecutionDTO toDTO(Execution e) {
        ExecutionDTO dto = ExecutionDTO.builder()
                .id(e.getId()).workflowId(e.getWorkflowId())
                .workflowVersion(e.getWorkflowVersion()).status(e.getStatus())
                .data(e.getData()).logs(e.getLogs()).currentStepId(e.getCurrentStepId())
                .retries(e.getRetries()).triggeredBy(e.getTriggeredBy())
                .startedAt(e.getStartedAt()).endedAt(e.getEndedAt()).createdAt(e.getCreatedAt()).build();
        if (e.getCurrentStepId() != null) {
            stepRepository.findById(e.getCurrentStepId()).ifPresent(s -> {
                dto.setCurrentStepName(s.getName());
                dto.setStepType(s.getStepType().name());
            });
        }
        workflowRepository.findById(e.getWorkflowId()).ifPresent(w -> dto.setWorkflowName(w.getName()));
        return dto;
    }
}
