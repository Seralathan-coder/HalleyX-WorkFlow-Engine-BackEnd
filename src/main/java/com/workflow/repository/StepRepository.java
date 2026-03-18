package com.workflow.repository;

import com.workflow.model.Step;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StepRepository extends JpaRepository<Step, String> {
    List<Step> findByWorkflowIdOrderByStepOrder(String workflowId);
    void deleteByWorkflowId(String workflowId);
    long countByWorkflowId(String workflowId);
}
