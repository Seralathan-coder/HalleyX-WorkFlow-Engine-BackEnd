package com.workflow.repository;

import com.workflow.model.Execution;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExecutionRepository extends JpaRepository<Execution, String> {
    List<Execution> findByWorkflowIdOrderByCreatedAtDesc(String workflowId);
    Page<Execution> findAllByOrderByCreatedAtDesc(Pageable pageable);
    List<Execution> findByStatus(Execution.ExecutionStatus status);
}
