package com.workflow.controller;

import com.workflow.dto.StepDTO;
import com.workflow.service.WorkflowService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class StepController {

    private final WorkflowService workflowService;

    public StepController(WorkflowService workflowService) {
        this.workflowService = workflowService;
    }

    @PostMapping("/api/workflows/{workflowId}/steps")
    public ResponseEntity<StepDTO> addStep(@PathVariable String workflowId, @RequestBody StepDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(workflowService.addStep(workflowId, dto));
    }

    @GetMapping("/api/workflows/{workflowId}/steps")
    public ResponseEntity<List<StepDTO>> listSteps(@PathVariable String workflowId) {
        return ResponseEntity.ok(workflowService.listSteps(workflowId));
    }

    @PutMapping("/api/steps/{id}")
    public ResponseEntity<StepDTO> updateStep(@PathVariable String id, @RequestBody StepDTO dto) {
        return ResponseEntity.ok(workflowService.updateStep(id, dto));
    }

    @DeleteMapping("/api/steps/{id}")
    public ResponseEntity<Void> deleteStep(@PathVariable String id) {
        workflowService.deleteStep(id);
        return ResponseEntity.noContent().build();
    }
}
