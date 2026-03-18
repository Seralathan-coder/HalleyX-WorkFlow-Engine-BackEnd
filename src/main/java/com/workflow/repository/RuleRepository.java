package com.workflow.repository;

import com.workflow.model.Rule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RuleRepository extends JpaRepository<Rule, String> {
    List<Rule> findByStepIdOrderByPriority(String stepId);
    void deleteByStepId(String stepId);
}
