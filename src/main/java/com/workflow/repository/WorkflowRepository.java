package com.workflow.repository;

import com.workflow.model.Workflow;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WorkflowRepository extends JpaRepository<Workflow, String> {
    Page<Workflow> findByNameContainingIgnoreCaseAndIsActive(String name, Boolean isActive, Pageable pageable);
    Page<Workflow> findByNameContainingIgnoreCase(String name, Pageable pageable);
    Page<Workflow> findByIsActive(Boolean isActive, Pageable pageable);
    List<Workflow> findByIsActive(Boolean isActive);
}
