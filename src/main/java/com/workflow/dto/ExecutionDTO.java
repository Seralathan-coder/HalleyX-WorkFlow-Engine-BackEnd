package com.workflow.dto;

import com.workflow.model.Execution;
import java.time.LocalDateTime;

public class ExecutionDTO {
    private String id;
    private String workflowId;
    private String workflowName;
    private Integer workflowVersion;
    private Execution.ExecutionStatus status;
    private String data;
    private String logs;
    private String currentStepId;
    private String currentStepName;
    private Integer retries;
    private String triggeredBy;
    private LocalDateTime startedAt;
    private LocalDateTime endedAt;
    private LocalDateTime createdAt;
    private String stepType;

    public ExecutionDTO() {}

    public static ExecutionDTOBuilder builder() { return new ExecutionDTOBuilder(); }

    public String getId() { return id; } public void setId(String id) { this.id = id; }
    public String getWorkflowId() { return workflowId; } public void setWorkflowId(String v) { this.workflowId = v; }
    public String getWorkflowName() { return workflowName; } public void setWorkflowName(String v) { this.workflowName = v; }
    public Integer getWorkflowVersion() { return workflowVersion; } public void setWorkflowVersion(Integer v) { this.workflowVersion = v; }
    public Execution.ExecutionStatus getStatus() { return status; } public void setStatus(Execution.ExecutionStatus status) { this.status = status; }
    public String getData() { return data; } public void setData(String data) { this.data = data; }
    public String getLogs() { return logs; } public void setLogs(String logs) { this.logs = logs; }
    public String getCurrentStepId() { return currentStepId; } public void setCurrentStepId(String v) { this.currentStepId = v; }
    public String getCurrentStepName() { return currentStepName; } public void setCurrentStepName(String v) { this.currentStepName = v; }
    public Integer getRetries() { return retries; } public void setRetries(Integer retries) { this.retries = retries; }
    public String getTriggeredBy() { return triggeredBy; } public void setTriggeredBy(String v) { this.triggeredBy = v; }
    public LocalDateTime getStartedAt() { return startedAt; } public void setStartedAt(LocalDateTime v) { this.startedAt = v; }
    public LocalDateTime getEndedAt() { return endedAt; } public void setEndedAt(LocalDateTime v) { this.endedAt = v; }
    public LocalDateTime getCreatedAt() { return createdAt; } public void setCreatedAt(LocalDateTime v) { this.createdAt = v; }
    public String getStepType() { return stepType; } public void setStepType(String stepType) { this.stepType = stepType; }

    public static class ExecutionDTOBuilder {
        private String id, workflowId, workflowName, data, logs, currentStepId, currentStepName, triggeredBy, stepType;
        private Integer workflowVersion, retries;
        private Execution.ExecutionStatus status;
        private LocalDateTime startedAt, endedAt, createdAt;
        public ExecutionDTOBuilder id(String v) { id = v; return this; }
        public ExecutionDTOBuilder workflowId(String v) { workflowId = v; return this; }
        public ExecutionDTOBuilder workflowName(String v) { workflowName = v; return this; }
        public ExecutionDTOBuilder workflowVersion(Integer v) { workflowVersion = v; return this; }
        public ExecutionDTOBuilder status(Execution.ExecutionStatus v) { status = v; return this; }
        public ExecutionDTOBuilder data(String v) { data = v; return this; }
        public ExecutionDTOBuilder logs(String v) { logs = v; return this; }
        public ExecutionDTOBuilder currentStepId(String v) { currentStepId = v; return this; }
        public ExecutionDTOBuilder currentStepName(String v) { currentStepName = v; return this; }
        public ExecutionDTOBuilder retries(Integer v) { retries = v; return this; }
        public ExecutionDTOBuilder triggeredBy(String v) { triggeredBy = v; return this; }
        public ExecutionDTOBuilder startedAt(LocalDateTime v) { startedAt = v; return this; }
        public ExecutionDTOBuilder endedAt(LocalDateTime v) { endedAt = v; return this; }
        public ExecutionDTOBuilder createdAt(LocalDateTime v) { createdAt = v; return this; }
        public ExecutionDTOBuilder stepType(String v) { stepType = v; return this; }
        public ExecutionDTO build() {
            ExecutionDTO d = new ExecutionDTO();
            d.id = id; d.workflowId = workflowId; d.workflowName = workflowName;
            d.workflowVersion = workflowVersion; d.status = status;
            d.data = data; d.logs = logs; d.currentStepId = currentStepId;
            d.currentStepName = currentStepName; d.retries = retries;
            d.triggeredBy = triggeredBy; d.startedAt = startedAt;
            d.endedAt = endedAt; d.createdAt = createdAt; d.stepType = stepType;
            return d;
        }
    }
}
