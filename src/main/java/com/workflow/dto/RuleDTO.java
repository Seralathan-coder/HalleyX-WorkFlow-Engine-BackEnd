package com.workflow.dto;

import java.time.LocalDateTime;

public class RuleDTO {
    private String id;
    private String stepId;
    private String condition;
    private String nextStepId;
    private String nextStepName;
    private Integer priority;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public RuleDTO() {}

    public static RuleDTOBuilder builder() { return new RuleDTOBuilder(); }

    public String getId() { return id; } public void setId(String id) { this.id = id; }
    public String getStepId() { return stepId; } public void setStepId(String stepId) { this.stepId = stepId; }
    public String getCondition() { return condition; } public void setCondition(String condition) { this.condition = condition; }
    public String getNextStepId() { return nextStepId; } public void setNextStepId(String nextStepId) { this.nextStepId = nextStepId; }
    public String getNextStepName() { return nextStepName; } public void setNextStepName(String nextStepName) { this.nextStepName = nextStepName; }
    public Integer getPriority() { return priority; } public void setPriority(Integer priority) { this.priority = priority; }
    public LocalDateTime getCreatedAt() { return createdAt; } public void setCreatedAt(LocalDateTime v) { this.createdAt = v; }
    public LocalDateTime getUpdatedAt() { return updatedAt; } public void setUpdatedAt(LocalDateTime v) { this.updatedAt = v; }

    public static class RuleDTOBuilder {
        private String id, stepId, condition, nextStepId, nextStepName;
        private Integer priority;
        private LocalDateTime createdAt, updatedAt;
        public RuleDTOBuilder id(String v) { id = v; return this; }
        public RuleDTOBuilder stepId(String v) { stepId = v; return this; }
        public RuleDTOBuilder condition(String v) { condition = v; return this; }
        public RuleDTOBuilder nextStepId(String v) { nextStepId = v; return this; }
        public RuleDTOBuilder nextStepName(String v) { nextStepName = v; return this; }
        public RuleDTOBuilder priority(Integer v) { priority = v; return this; }
        public RuleDTOBuilder createdAt(LocalDateTime v) { createdAt = v; return this; }
        public RuleDTOBuilder updatedAt(LocalDateTime v) { updatedAt = v; return this; }
        public RuleDTO build() {
            RuleDTO d = new RuleDTO();
            d.id = id; d.stepId = stepId; d.condition = condition; d.nextStepId = nextStepId;
            d.nextStepName = nextStepName; d.priority = priority;
            d.createdAt = createdAt; d.updatedAt = updatedAt;
            return d;
        }
    }
}
