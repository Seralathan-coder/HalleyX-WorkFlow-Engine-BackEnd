package com.workflow.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class NotificationService {

    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);

    public String sendNotification(String channel, String assignee, String message, String executionId) {
        String logEntry = String.format(
                "[%s] NOTIFICATION sent via %s to %s: %s",
                LocalDateTime.now(),
                channel != null ? channel : "default",
                assignee != null ? assignee : "team",
                message != null ? message : "Workflow notification"
        );
        log.info("[NOTIFICATION] Execution={} | Channel={} | Assignee={} | Message={}",
                executionId, channel, assignee, message);
        return logEntry;
    }
}
