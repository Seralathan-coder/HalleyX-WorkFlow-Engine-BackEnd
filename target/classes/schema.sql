CREATE DATABASE IF NOT EXISTS workflow_engine;
USE workflow_engine;

CREATE TABLE IF NOT EXISTS workflows (
    id CHAR(36) NOT NULL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    version INTEGER NOT NULL DEFAULT 1,
    is_active BOOLEAN DEFAULT true,
    input_schema LONGTEXT,
    start_step_id CHAR(36),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS steps (
    id CHAR(36) NOT NULL PRIMARY KEY,
    workflow_id CHAR(36) NOT NULL,
    name VARCHAR(255) NOT NULL,
    step_type ENUM('task', 'approval', 'notification') NOT NULL,
    step_order INTEGER NOT NULL,
    metadata LONGTEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (workflow_id) REFERENCES workflows(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS rules (
    id CHAR(36) NOT NULL PRIMARY KEY,
    step_id CHAR(36) NOT NULL,
    rule_condition TEXT NOT NULL,
    next_step_id CHAR(36),
    priority INTEGER NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (step_id) REFERENCES steps(id) ON DELETE CASCADE,
    FOREIGN KEY (next_step_id) REFERENCES steps(id) ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS executions (
    id CHAR(36) NOT NULL PRIMARY KEY,
    workflow_id CHAR(36) NOT NULL,
    workflow_version INTEGER NOT NULL,
    status ENUM('pending', 'in_progress', 'completed', 'failed', 'canceled') DEFAULT 'pending',
    data LONGTEXT,
    logs LONGTEXT,
    current_step_id CHAR(36),
    retries INTEGER DEFAULT 0,
    triggered_by VARCHAR(255),
    started_at TIMESTAMP NULL,
    ended_at TIMESTAMP NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (workflow_id) REFERENCES workflows(id)
);
