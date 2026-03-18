package com.workflow.controller;

import com.workflow.dto.WorkflowDTO;
import com.workflow.service.WorkflowService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/workflows")
public class WorkflowController {

    private final WorkflowService workflowService;

    public WorkflowController(WorkflowService workflowService) {
        this.workflowService = workflowService;
    }

    @PostMapping
    public ResponseEntity<WorkflowDTO> create(@RequestBody WorkflowDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(workflowService.createWorkflow(dto));
    }

    @GetMapping
    public ResponseEntity<Page<WorkflowDTO>> list(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Boolean isActive,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        PageRequest pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return ResponseEntity.ok(workflowService.listWorkflows(name, isActive, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<WorkflowDTO> getById(@PathVariable String id) {
        return ResponseEntity.ok(workflowService.getWorkflowById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<WorkflowDTO> update(@PathVariable String id, @RequestBody WorkflowDTO dto) {
        return ResponseEntity.ok(workflowService.updateWorkflow(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        workflowService.deleteWorkflow(id);
        return ResponseEntity.noContent().build();
    }
}
