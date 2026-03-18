package com.workflow.model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "workflows")
public class Workflow {

    @Id
    @Column(name = "id", length = 36)
    private String id;

    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @Column(name = "version", nullable = false)
    private Integer version;

    @Column(name = "is_active")
    private Boolean isActive;

    @Column(name = "input_schema", columnDefinition = "LONGTEXT")
    private String inputSchema;

    @Column(name = "start_step_id", length = 36)
    private String startStepId;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public Workflow() {}

    public Workflow(String id, String name, Integer version, Boolean isActive, String inputSchema, String startStepId) {
        this.id = id; this.name = name; this.version = version;
        this.isActive = isActive; this.inputSchema = inputSchema; this.startStepId = startStepId;
    }

    public static WorkflowBuilder builder() { return new WorkflowBuilder(); }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Integer getVersion() { return version; }
    public void setVersion(Integer version) { this.version = version; }
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
    public String getInputSchema() { return inputSchema; }
    public void setInputSchema(String inputSchema) { this.inputSchema = inputSchema; }
    public String getStartStepId() { return startStepId; }
    public void setStartStepId(String startStepId) { this.startStepId = startStepId; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public static class WorkflowBuilder {
        private String id, name, inputSchema, startStepId;
        private Integer version;
        private Boolean isActive;
        public WorkflowBuilder id(String v) { id = v; return this; }
        public WorkflowBuilder name(String v) { name = v; return this; }
        public WorkflowBuilder version(Integer v) { version = v; return this; }
        public WorkflowBuilder isActive(Boolean v) { isActive = v; return this; }
        public WorkflowBuilder inputSchema(String v) { inputSchema = v; return this; }
        public WorkflowBuilder startStepId(String v) { startStepId = v; return this; }
        public Workflow build() { return new Workflow(id, name, version, isActive, inputSchema, startStepId); }
    }
}
