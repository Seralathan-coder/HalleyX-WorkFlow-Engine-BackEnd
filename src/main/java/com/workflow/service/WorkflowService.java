package com.workflow.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.workflow.dto.WorkflowDTO;
import com.workflow.dto.StepDTO;
import com.workflow.dto.RuleDTO;
import com.workflow.model.Workflow;
import com.workflow.model.Step;
import com.workflow.model.Rule;
import com.workflow.repository.WorkflowRepository;
import com.workflow.repository.StepRepository;
import com.workflow.repository.RuleRepository;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class WorkflowService {

    private static final Logger log = LoggerFactory.getLogger(WorkflowService.class);

    private final WorkflowRepository workflowRepository;
    private final StepRepository stepRepository;
    private final RuleRepository ruleRepository;

    public WorkflowService(WorkflowRepository workflowRepository,
                           StepRepository stepRepository,
                           RuleRepository ruleRepository) {
        this.workflowRepository = workflowRepository;
        this.stepRepository = stepRepository;
        this.ruleRepository = ruleRepository;
    }

    @Transactional
    public WorkflowDTO createWorkflow(WorkflowDTO dto) {
        Workflow workflow = Workflow.builder()
                .id(UUID.randomUUID().toString())
                .name(dto.getName())
                .version(1)
                .isActive(true)
                .inputSchema(dto.getInputSchema())
                .startStepId(dto.getStartStepId())
                .build();
        Workflow saved = workflowRepository.save(workflow);
        log.info("Created workflow: id={} name={}", saved.getId(), saved.getName());
        return toDTO(saved, false);
    }

    public Page<WorkflowDTO> listWorkflows(String name, Boolean isActive, Pageable pageable) {
        Page<Workflow> page;
        if (name != null && !name.isBlank() && isActive != null) {
            page = workflowRepository.findByNameContainingIgnoreCaseAndIsActive(name, isActive, pageable);
        } else if (name != null && !name.isBlank()) {
            page = workflowRepository.findByNameContainingIgnoreCase(name, pageable);
        } else if (isActive != null) {
            page = workflowRepository.findByIsActive(isActive, pageable);
        } else {
            page = workflowRepository.findAll(pageable);
        }
        return page.map(w -> {
            WorkflowDTO d = toDTO(w, false);
            d.setStepCount((int) stepRepository.countByWorkflowId(w.getId()));
            return d;
        });
    }

    public WorkflowDTO getWorkflowById(String id) {
        Workflow workflow = workflowRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Workflow not found: " + id));
        return toDTO(workflow, true);
    }

    @Transactional
    public WorkflowDTO updateWorkflow(String id, WorkflowDTO dto) {
        Workflow existing = workflowRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Workflow not found: " + id));
        existing.setIsActive(false);
        workflowRepository.save(existing);

        Workflow newVersion = Workflow.builder()
                .id(UUID.randomUUID().toString())
                .name(dto.getName() != null ? dto.getName() : existing.getName())
                .version(existing.getVersion() + 1)
                .isActive(true)
                .inputSchema(dto.getInputSchema() != null ? dto.getInputSchema() : existing.getInputSchema())
                .startStepId(dto.getStartStepId() != null ? dto.getStartStepId() : existing.getStartStepId())
                .build();
        Workflow saved = workflowRepository.save(newVersion);
        log.info("Updated workflow: new id={} version={}", saved.getId(), saved.getVersion());
        return toDTO(saved, false);
    }

    @Transactional
    public void deleteWorkflow(String id) {
        Workflow workflow = workflowRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Workflow not found: " + id));
        workflow.setIsActive(false);
        workflowRepository.save(workflow);
        log.info("Soft-deleted workflow: id={}", id);
    }

    // ---- Steps ----

    @Transactional
    public StepDTO addStep(String workflowId, StepDTO dto) {
        workflowRepository.findById(workflowId)
                .orElseThrow(() -> new EntityNotFoundException("Workflow not found: " + workflowId));
        List<Step> existing = stepRepository.findByWorkflowIdOrderByStepOrder(workflowId);
        int order = dto.getStepOrder() != null ? dto.getStepOrder() : existing.size() + 1;
        Step step = Step.builder()
                .id(UUID.randomUUID().toString())
                .workflowId(workflowId).name(dto.getName())
                .stepType(dto.getStepType()).stepOrder(order)
                .metadata(dto.getMetadata()).build();
        Step saved = stepRepository.save(step);
        if (existing.isEmpty()) {
            Workflow wf = workflowRepository.findById(workflowId).get();
            wf.setStartStepId(saved.getId());
            workflowRepository.save(wf);
        }
        log.info("Added step: id={} workflowId={} name={}", saved.getId(), workflowId, saved.getName());
        return toStepDTO(saved);
    }

    public List<StepDTO> listSteps(String workflowId) {
        return stepRepository.findByWorkflowIdOrderByStepOrder(workflowId).stream().map(s -> {
            StepDTO d = toStepDTO(s);
            d.setRules(ruleRepository.findByStepIdOrderByPriority(s.getId()).stream()
                    .map(this::toRuleDTO).collect(Collectors.toList()));
            return d;
        }).collect(Collectors.toList());
    }

    @Transactional
    public StepDTO updateStep(String stepId, StepDTO dto) {
        Step step = stepRepository.findById(stepId)
                .orElseThrow(() -> new EntityNotFoundException("Step not found: " + stepId));
        if (dto.getName() != null) step.setName(dto.getName());
        if (dto.getStepType() != null) step.setStepType(dto.getStepType());
        if (dto.getStepOrder() != null) step.setStepOrder(dto.getStepOrder());
        if (dto.getMetadata() != null) step.setMetadata(dto.getMetadata());
        return toStepDTO(stepRepository.save(step));
    }

    @Transactional
    public void deleteStep(String stepId) {
        stepRepository.findById(stepId)
                .orElseThrow(() -> new EntityNotFoundException("Step not found: " + stepId));
        ruleRepository.deleteByStepId(stepId);
        stepRepository.deleteById(stepId);
        log.info("Deleted step: id={}", stepId);
    }

    // ---- Rules ----

    @Transactional
    public RuleDTO addRule(String stepId, RuleDTO dto) {
        stepRepository.findById(stepId)
                .orElseThrow(() -> new EntityNotFoundException("Step not found: " + stepId));
        Rule rule = Rule.builder()
                .id(UUID.randomUUID().toString()).stepId(stepId)
                .condition(dto.getCondition()).nextStepId(dto.getNextStepId())
                .priority(dto.getPriority()).build();
        Rule saved = ruleRepository.save(rule);
        log.info("Added rule: id={} stepId={}", saved.getId(), stepId);
        return toRuleDTO(saved);
    }

    public List<RuleDTO> listRules(String stepId) {
        return ruleRepository.findByStepIdOrderByPriority(stepId).stream()
                .map(this::toRuleDTO).collect(Collectors.toList());
    }

    @Transactional
    public RuleDTO updateRule(String ruleId, RuleDTO dto) {
        Rule rule = ruleRepository.findById(ruleId)
                .orElseThrow(() -> new EntityNotFoundException("Rule not found: " + ruleId));
        if (dto.getCondition() != null) rule.setCondition(dto.getCondition());
        if (dto.getNextStepId() != null) rule.setNextStepId(dto.getNextStepId());
        if (dto.getPriority() != null) rule.setPriority(dto.getPriority());
        return toRuleDTO(ruleRepository.save(rule));
    }

    @Transactional
    public void deleteRule(String ruleId) {
        ruleRepository.findById(ruleId)
                .orElseThrow(() -> new EntityNotFoundException("Rule not found: " + ruleId));
        ruleRepository.deleteById(ruleId);
    }

    // ---- Mapping ----

    public WorkflowDTO toDTO(Workflow w, boolean includeSteps) {
        WorkflowDTO dto = WorkflowDTO.builder()
                .id(w.getId()).name(w.getName()).version(w.getVersion())
                .isActive(w.getIsActive()).inputSchema(w.getInputSchema())
                .startStepId(w.getStartStepId())
                .createdAt(w.getCreatedAt()).updatedAt(w.getUpdatedAt()).build();
        if (includeSteps) {
            dto.setSteps(listSteps(w.getId()));
            dto.setStepCount(dto.getSteps().size());
        } else {
            dto.setStepCount((int) stepRepository.countByWorkflowId(w.getId()));
        }
        return dto;
    }

    public StepDTO toStepDTO(Step s) {
        return StepDTO.builder()
                .id(s.getId()).workflowId(s.getWorkflowId()).name(s.getName())
                .stepType(s.getStepType()).stepOrder(s.getStepOrder()).metadata(s.getMetadata())
                .createdAt(s.getCreatedAt()).updatedAt(s.getUpdatedAt()).build();
    }

    public RuleDTO toRuleDTO(Rule r) {
        RuleDTO dto = RuleDTO.builder()
                .id(r.getId()).stepId(r.getStepId()).condition(r.getCondition())
                .nextStepId(r.getNextStepId()).priority(r.getPriority())
                .createdAt(r.getCreatedAt()).updatedAt(r.getUpdatedAt()).build();
        if (r.getNextStepId() != null) {
            stepRepository.findById(r.getNextStepId()).ifPresent(s -> dto.setNextStepName(s.getName()));
        } else {
            dto.setNextStepName("End");
        }
        return dto;
    }
}
