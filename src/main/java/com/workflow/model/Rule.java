package com.workflow.model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "rules")
public class Rule {

    @Id
    @Column(name = "id", length = 36)
    private String id;

    @Column(name = "step_id", nullable = false, length = 36)
    private String stepId;

    @Column(name = "rule_condition", nullable = false, columnDefinition = "TEXT")
    private String condition;

    @Column(name = "next_step_id", length = 36)
    private String nextStepId;

    @Column(name = "priority", nullable = false)
    private Integer priority;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public Rule() {}

    public Rule(String id, String stepId, String condition, String nextStepId, Integer priority) {
        this.id = id; this.stepId = stepId; this.condition = condition;
        this.nextStepId = nextStepId; this.priority = priority;
    }

    public static RuleBuilder builder() { return new RuleBuilder(); }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getStepId() { return stepId; }
    public void setStepId(String stepId) { this.stepId = stepId; }
    public String getCondition() { return condition; }
    public void setCondition(String condition) { this.condition = condition; }
    public String getNextStepId() { return nextStepId; }
    public void setNextStepId(String nextStepId) { this.nextStepId = nextStepId; }
    public Integer getPriority() { return priority; }
    public void setPriority(Integer priority) { this.priority = priority; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime v) { this.createdAt = v; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime v) { this.updatedAt = v; }

    public static class RuleBuilder {
        private String id, stepId, condition, nextStepId;
        private Integer priority;
        public RuleBuilder id(String v) { id = v; return this; }
        public RuleBuilder stepId(String v) { stepId = v; return this; }
        public RuleBuilder condition(String v) { condition = v; return this; }
        public RuleBuilder nextStepId(String v) { nextStepId = v; return this; }
        public RuleBuilder priority(Integer v) { priority = v; return this; }
        public Rule build() { return new Rule(id, stepId, condition, nextStepId, priority); }
    }
}
