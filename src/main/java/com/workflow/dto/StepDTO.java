package com.workflow.dto;

import com.workflow.model.Step;
import java.time.LocalDateTime;
import java.util.List;

public class StepDTO {
    private String id;
    private String workflowId;
    private String name;
    private Step.StepType stepType;
    private Integer stepOrder;
    private String metadata;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<RuleDTO> rules;

    public StepDTO() {}

    public static StepDTOBuilder builder() { return new StepDTOBuilder(); }

    public String getId() { return id; } public void setId(String id) { this.id = id; }
    public String getWorkflowId() { return workflowId; } public void setWorkflowId(String v) { this.workflowId = v; }
    public String getName() { return name; } public void setName(String name) { this.name = name; }
    public Step.StepType getStepType() { return stepType; } public void setStepType(Step.StepType stepType) { this.stepType = stepType; }
    public Integer getStepOrder() { return stepOrder; } public void setStepOrder(Integer stepOrder) { this.stepOrder = stepOrder; }
    public String getMetadata() { return metadata; } public void setMetadata(String metadata) { this.metadata = metadata; }
    public LocalDateTime getCreatedAt() { return createdAt; } public void setCreatedAt(LocalDateTime v) { this.createdAt = v; }
    public LocalDateTime getUpdatedAt() { return updatedAt; } public void setUpdatedAt(LocalDateTime v) { this.updatedAt = v; }
    public List<RuleDTO> getRules() { return rules; } public void setRules(List<RuleDTO> rules) { this.rules = rules; }

    public static class StepDTOBuilder {
        private String id, workflowId, name, metadata;
        private Step.StepType stepType;
        private Integer stepOrder;
        private LocalDateTime createdAt, updatedAt;
        private List<RuleDTO> rules;
        public StepDTOBuilder id(String v) { id = v; return this; }
        public StepDTOBuilder workflowId(String v) { workflowId = v; return this; }
        public StepDTOBuilder name(String v) { name = v; return this; }
        public StepDTOBuilder stepType(Step.StepType v) { stepType = v; return this; }
        public StepDTOBuilder stepOrder(Integer v) { stepOrder = v; return this; }
        public StepDTOBuilder metadata(String v) { metadata = v; return this; }
        public StepDTOBuilder createdAt(LocalDateTime v) { createdAt = v; return this; }
        public StepDTOBuilder updatedAt(LocalDateTime v) { updatedAt = v; return this; }
        public StepDTOBuilder rules(List<RuleDTO> v) { rules = v; return this; }
        public StepDTO build() {
            StepDTO d = new StepDTO();
            d.id = id; d.workflowId = workflowId; d.name = name; d.stepType = stepType;
            d.stepOrder = stepOrder; d.metadata = metadata;
            d.createdAt = createdAt; d.updatedAt = updatedAt; d.rules = rules;
            return d;
        }
    }
}
