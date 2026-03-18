package com.workflow.model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "executions")
public class Execution {

    @Id
    @Column(name = "id", length = 36)
    private String id;

    @Column(name = "workflow_id", nullable = false, length = 36)
    private String workflowId;

    @Column(name = "workflow_version", nullable = false)
    private Integer workflowVersion;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", columnDefinition = "ENUM('pending','in_progress','completed','failed','canceled')")
    private ExecutionStatus status;

    @Column(name = "data", columnDefinition = "LONGTEXT")
    private String data;

    @Column(name = "logs", columnDefinition = "LONGTEXT")
    private String logs;

    @Column(name = "current_step_id", length = 36)
    private String currentStepId;

    @Column(name = "retries")
    private Integer retries;

    @Column(name = "triggered_by", length = 255)
    private String triggeredBy;

    @Column(name = "started_at")
    private LocalDateTime startedAt;

    @Column(name = "ended_at")
    private LocalDateTime endedAt;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    public enum ExecutionStatus { pending, in_progress, completed, failed, canceled }

    public Execution() {}

    public static ExecutionBuilder builder() { return new ExecutionBuilder(); }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getWorkflowId() { return workflowId; }
    public void setWorkflowId(String workflowId) { this.workflowId = workflowId; }
    public Integer getWorkflowVersion() { return workflowVersion; }
    public void setWorkflowVersion(Integer workflowVersion) { this.workflowVersion = workflowVersion; }
    public ExecutionStatus getStatus() { return status; }
    public void setStatus(ExecutionStatus status) { this.status = status; }
    public String getData() { return data; }
    public void setData(String data) { this.data = data; }
    public String getLogs() { return logs; }
    public void setLogs(String logs) { this.logs = logs; }
    public String getCurrentStepId() { return currentStepId; }
    public void setCurrentStepId(String currentStepId) { this.currentStepId = currentStepId; }
    public Integer getRetries() { return retries; }
    public void setRetries(Integer retries) { this.retries = retries; }
    public String getTriggeredBy() { return triggeredBy; }
    public void setTriggeredBy(String triggeredBy) { this.triggeredBy = triggeredBy; }
    public LocalDateTime getStartedAt() { return startedAt; }
    public void setStartedAt(LocalDateTime startedAt) { this.startedAt = startedAt; }
    public LocalDateTime getEndedAt() { return endedAt; }
    public void setEndedAt(LocalDateTime endedAt) { this.endedAt = endedAt; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public static class ExecutionBuilder {
        private String id, workflowId, data, logs, currentStepId, triggeredBy;
        private Integer workflowVersion, retries;
        private ExecutionStatus status;
        private LocalDateTime startedAt, endedAt;

        public ExecutionBuilder id(String v) { id = v; return this; }
        public ExecutionBuilder workflowId(String v) { workflowId = v; return this; }
        public ExecutionBuilder workflowVersion(Integer v) { workflowVersion = v; return this; }
        public ExecutionBuilder status(ExecutionStatus v) { status = v; return this; }
        public ExecutionBuilder data(String v) { data = v; return this; }
        public ExecutionBuilder logs(String v) { logs = v; return this; }
        public ExecutionBuilder currentStepId(String v) { currentStepId = v; return this; }
        public ExecutionBuilder retries(Integer v) { retries = v; return this; }
        public ExecutionBuilder triggeredBy(String v) { triggeredBy = v; return this; }
        public ExecutionBuilder startedAt(LocalDateTime v) { startedAt = v; return this; }
        public ExecutionBuilder endedAt(LocalDateTime v) { endedAt = v; return this; }

        public Execution build() {
            Execution e = new Execution();
            e.id = id; e.workflowId = workflowId; e.workflowVersion = workflowVersion;
            e.status = status; e.data = data; e.logs = logs; e.currentStepId = currentStepId;
            e.retries = retries; e.triggeredBy = triggeredBy;
            e.startedAt = startedAt; e.endedAt = endedAt;
            return e;
        }
    }
}
