package com.workflow.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.workflow.dto.ExecutionDTO;
import com.workflow.service.ExecutionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
public class ExecutionController {

    private final ExecutionService executionService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public ExecutionController(ExecutionService executionService) {
        this.executionService = executionService;
    }

    @PostMapping("/api/workflows/{workflowId}/execute")
    public ResponseEntity<ExecutionDTO> execute(
            @PathVariable String workflowId,
            @RequestBody(required = false) Map<String, Object> body) {
        String dataJson = null;
        String triggeredBy = "user";
        if (body != null) {
            Object dataField = body.get("data");
            if (dataField instanceof String s) {
                dataJson = s;
            } else if (dataField != null) {
                try { dataJson = objectMapper.writeValueAsString(dataField); } catch (Exception ignored) {}
            }
            Object tb = body.get("triggeredBy");
            if (tb instanceof String s) triggeredBy = s;
        }
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(executionService.startExecution(workflowId, dataJson, triggeredBy));
    }

    @GetMapping("/api/executions/{id}")
    public ResponseEntity<ExecutionDTO> getExecution(@PathVariable String id) {
        return ResponseEntity.ok(executionService.getExecution(id));
    }

    @GetMapping("/api/executions")
    public ResponseEntity<Page<ExecutionDTO>> listExecutions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(executionService.listAllExecutions(
                PageRequest.of(page, size, Sort.by("createdAt").descending())));
    }

    @GetMapping("/api/workflows/{workflowId}/executions")
    public ResponseEntity<List<ExecutionDTO>> listByWorkflow(@PathVariable String workflowId) {
        return ResponseEntity.ok(executionService.listExecutionsByWorkflow(workflowId));
    }

    @PostMapping("/api/executions/{id}/cancel")
    public ResponseEntity<ExecutionDTO> cancel(@PathVariable String id) {
        return ResponseEntity.ok(executionService.cancelExecution(id));
    }

    @PostMapping("/api/executions/{id}/retry")
    public ResponseEntity<ExecutionDTO> retry(@PathVariable String id) {
        return ResponseEntity.ok(executionService.retryExecution(id));
    }

    @PostMapping("/api/executions/{id}/approve")
    public ResponseEntity<ExecutionDTO> approve(@PathVariable String id) {
        return ResponseEntity.ok(executionService.approveStep(id, true));
    }

    @PostMapping("/api/executions/{id}/reject")
    public ResponseEntity<ExecutionDTO> reject(@PathVariable String id) {
        return ResponseEntity.ok(executionService.approveStep(id, false));
    }
}
