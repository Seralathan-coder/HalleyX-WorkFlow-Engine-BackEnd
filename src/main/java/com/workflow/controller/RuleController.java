package com.workflow.controller;

import com.workflow.dto.RuleDTO;
import com.workflow.service.WorkflowService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class RuleController {

    private final WorkflowService workflowService;

    public RuleController(WorkflowService workflowService) {
        this.workflowService = workflowService;
    }

    @PostMapping("/api/steps/{stepId}/rules")
    public ResponseEntity<RuleDTO> addRule(@PathVariable String stepId, @RequestBody RuleDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(workflowService.addRule(stepId, dto));
    }

    @GetMapping("/api/steps/{stepId}/rules")
    public ResponseEntity<List<RuleDTO>> listRules(@PathVariable String stepId) {
        return ResponseEntity.ok(workflowService.listRules(stepId));
    }

    @PutMapping("/api/rules/{id}")
    public ResponseEntity<RuleDTO> updateRule(@PathVariable String id, @RequestBody RuleDTO dto) {
        return ResponseEntity.ok(workflowService.updateRule(id, dto));
    }

    @DeleteMapping("/api/rules/{id}")
    public ResponseEntity<Void> deleteRule(@PathVariable String id) {
        workflowService.deleteRule(id);
        return ResponseEntity.noContent().build();
    }
}
