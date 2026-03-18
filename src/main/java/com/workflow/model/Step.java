package com.workflow.model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "steps")
public class Step {

    @Id
    @Column(name = "id", length = 36)
    private String id;

    @Column(name = "workflow_id", nullable = false, length = 36)
    private String workflowId;

    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "step_type", nullable = false, columnDefinition = "ENUM('task','approval','notification')")
    private StepType stepType;

    @Column(name = "step_order", nullable = false)
    private Integer stepOrder;

    @Column(name = "metadata", columnDefinition = "LONGTEXT")
    private String metadata;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public enum StepType { task, approval, notification }

    public Step() {}

    public Step(String id, String workflowId, String name, StepType stepType, Integer stepOrder, String metadata) {
        this.id = id; this.workflowId = workflowId; this.name = name;
        this.stepType = stepType; this.stepOrder = stepOrder; this.metadata = metadata;
    }

    public static StepBuilder builder() { return new StepBuilder(); }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getWorkflowId() { return workflowId; }
    public void setWorkflowId(String workflowId) { this.workflowId = workflowId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public StepType getStepType() { return stepType; }
    public void setStepType(StepType stepType) { this.stepType = stepType; }
    public Integer getStepOrder() { return stepOrder; }
    public void setStepOrder(Integer stepOrder) { this.stepOrder = stepOrder; }
    public String getMetadata() { return metadata; }
    public void setMetadata(String metadata) { this.metadata = metadata; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime v) { this.createdAt = v; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime v) { this.updatedAt = v; }

    public static class StepBuilder {
        private String id, workflowId, name, metadata;
        private StepType stepType;
        private Integer stepOrder;
        public StepBuilder id(String v) { id = v; return this; }
        public StepBuilder workflowId(String v) { workflowId = v; return this; }
        public StepBuilder name(String v) { name = v; return this; }
        public StepBuilder stepType(StepType v) { stepType = v; return this; }
        public StepBuilder stepOrder(Integer v) { stepOrder = v; return this; }
        public StepBuilder metadata(String v) { metadata = v; return this; }
        public Step build() { return new Step(id, workflowId, name, stepType, stepOrder, metadata); }
    }
}
