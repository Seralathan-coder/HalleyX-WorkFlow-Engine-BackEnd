package com.workflow.dto;

import java.time.LocalDateTime;
import java.util.List;

public class WorkflowDTO {
    private String id;
    private String name;
    private Integer version;
    private Boolean isActive;
    private String inputSchema;
    private String startStepId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<StepDTO> steps;
    private Integer stepCount;

    public WorkflowDTO() {}

    public static WorkflowDTOBuilder builder() { return new WorkflowDTOBuilder(); }

    public String getId() { return id; } public void setId(String id) { this.id = id; }
    public String getName() { return name; } public void setName(String name) { this.name = name; }
    public Integer getVersion() { return version; } public void setVersion(Integer version) { this.version = version; }
    public Boolean getIsActive() { return isActive; } public void setIsActive(Boolean isActive) { this.isActive = isActive; }
    public String getInputSchema() { return inputSchema; } public void setInputSchema(String inputSchema) { this.inputSchema = inputSchema; }
    public String getStartStepId() { return startStepId; } public void setStartStepId(String startStepId) { this.startStepId = startStepId; }
    public LocalDateTime getCreatedAt() { return createdAt; } public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; } public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    public List<StepDTO> getSteps() { return steps; } public void setSteps(List<StepDTO> steps) { this.steps = steps; }
    public Integer getStepCount() { return stepCount; } public void setStepCount(Integer stepCount) { this.stepCount = stepCount; }

    public static class WorkflowDTOBuilder {
        private String id, name, inputSchema, startStepId;
        private Integer version, stepCount;
        private Boolean isActive;
        private LocalDateTime createdAt, updatedAt;
        private List<StepDTO> steps;
        public WorkflowDTOBuilder id(String v) { id = v; return this; }
        public WorkflowDTOBuilder name(String v) { name = v; return this; }
        public WorkflowDTOBuilder version(Integer v) { version = v; return this; }
        public WorkflowDTOBuilder isActive(Boolean v) { isActive = v; return this; }
        public WorkflowDTOBuilder inputSchema(String v) { inputSchema = v; return this; }
        public WorkflowDTOBuilder startStepId(String v) { startStepId = v; return this; }
        public WorkflowDTOBuilder createdAt(LocalDateTime v) { createdAt = v; return this; }
        public WorkflowDTOBuilder updatedAt(LocalDateTime v) { updatedAt = v; return this; }
        public WorkflowDTOBuilder steps(List<StepDTO> v) { steps = v; return this; }
        public WorkflowDTOBuilder stepCount(Integer v) { stepCount = v; return this; }
        public WorkflowDTO build() {
            WorkflowDTO d = new WorkflowDTO();
            d.id = id; d.name = name; d.version = version; d.isActive = isActive;
            d.inputSchema = inputSchema; d.startStepId = startStepId;
            d.createdAt = createdAt; d.updatedAt = updatedAt;
            d.steps = steps; d.stepCount = stepCount;
            return d;
        }
    }
}
