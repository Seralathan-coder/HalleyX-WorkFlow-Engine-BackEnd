package com.workflow.config;

import com.workflow.dto.RuleDTO;
import com.workflow.dto.StepDTO;
import com.workflow.dto.WorkflowDTO;
import com.workflow.model.Step;
import com.workflow.repository.WorkflowRepository;
import com.workflow.service.WorkflowService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final WorkflowRepository workflowRepository;
    private final WorkflowService workflowService;

    public DataInitializer(WorkflowRepository workflowRepository, WorkflowService workflowService) {
        this.workflowRepository = workflowRepository;
        this.workflowService = workflowService;
    }

    @Override
    public void run(String... args) {
        if (workflowRepository.count() > 0) {
            log.info("Database already has data, skipping seed.");
            return;
        }
        log.info("Seeding sample workflows...");
        seedExpenseApproval();
        seedEmployeeOnboarding();
        log.info("Seeding complete.");
    }

    private void seedExpenseApproval() {
        String inputSchema = "{\"type\":\"object\",\"properties\":{\"amount\":{\"type\":\"number\",\"minimum\":0},\"country\":{\"type\":\"string\",\"enum\":[\"US\",\"UK\",\"IN\",\"CA\"]},\"priority\":{\"type\":\"string\",\"enum\":[\"Low\",\"Medium\",\"High\"]},\"description\":{\"type\":\"string\"}},\"required\":[\"amount\",\"country\",\"priority\"]}";

        WorkflowDTO wf = workflowService.createWorkflow(WorkflowDTO.builder().name("Expense Approval").inputSchema(inputSchema).build());

        StepDTO s1 = workflowService.addStep(wf.getId(), StepDTO.builder().name("Manager Approval").stepType(Step.StepType.approval).stepOrder(1).metadata("{\"assignee\":\"manager@company.com\"}").build());
        StepDTO s2 = workflowService.addStep(wf.getId(), StepDTO.builder().name("Finance Notification").stepType(Step.StepType.notification).stepOrder(2).metadata("{\"channel\":\"email\"}").build());
        StepDTO s3 = workflowService.addStep(wf.getId(), StepDTO.builder().name("CEO Approval").stepType(Step.StepType.approval).stepOrder(3).metadata("{\"assignee\":\"ceo@company.com\"}").build());
        StepDTO s4 = workflowService.addStep(wf.getId(), StepDTO.builder().name("Task Completion").stepType(Step.StepType.task).stepOrder(4).metadata("{\"action\":\"update_database\"}").build());
        StepDTO s5 = workflowService.addStep(wf.getId(), StepDTO.builder().name("Task Rejection").stepType(Step.StepType.notification).stepOrder(5).metadata("{\"message\":\"Expense rejected\"}").build());

        workflowService.addRule(s1.getId(), RuleDTO.builder().condition("amount > 1000 && priority == 'High'").nextStepId(s3.getId()).priority(1).build());
        workflowService.addRule(s1.getId(), RuleDTO.builder().condition("amount > 500 && country == 'US'").nextStepId(s2.getId()).priority(2).build());
        workflowService.addRule(s1.getId(), RuleDTO.builder().condition("amount <= 500").nextStepId(s4.getId()).priority(3).build());
        workflowService.addRule(s1.getId(), RuleDTO.builder().condition("DEFAULT").nextStepId(s5.getId()).priority(4).build());
        workflowService.addRule(s2.getId(), RuleDTO.builder().condition("DEFAULT").nextStepId(s4.getId()).priority(1).build());
        workflowService.addRule(s3.getId(), RuleDTO.builder().condition("DEFAULT").nextStepId(s4.getId()).priority(1).build());

        log.info("Seeded 'Expense Approval' workflow: id={}", wf.getId());
    }

    private void seedEmployeeOnboarding() {
        String inputSchema = "{\"type\":\"object\",\"properties\":{\"department\":{\"type\":\"string\",\"enum\":[\"Engineering\",\"Sales\",\"HR\",\"Marketing\"]},\"role\":{\"type\":\"string\"},\"startDate\":{\"type\":\"string\",\"format\":\"date\"}},\"required\":[\"department\",\"role\"]}";

        WorkflowDTO wf = workflowService.createWorkflow(WorkflowDTO.builder().name("Employee Onboarding").inputSchema(inputSchema).build());

        StepDTO s1 = workflowService.addStep(wf.getId(), StepDTO.builder().name("HR Check").stepType(Step.StepType.task).stepOrder(1).metadata("{\"check\":\"background_verification\"}").build());
        StepDTO s2 = workflowService.addStep(wf.getId(), StepDTO.builder().name("IT Setup").stepType(Step.StepType.task).stepOrder(2).metadata("{\"resources\":[\"laptop\",\"email\"]}").build());
        StepDTO s3 = workflowService.addStep(wf.getId(), StepDTO.builder().name("Manager Notification").stepType(Step.StepType.notification).stepOrder(3).metadata("{\"channel\":\"slack\"}").build());

        workflowService.addRule(s1.getId(), RuleDTO.builder().condition("department == 'Engineering'").nextStepId(s2.getId()).priority(1).build());
        workflowService.addRule(s1.getId(), RuleDTO.builder().condition("DEFAULT").nextStepId(s3.getId()).priority(2).build());
        workflowService.addRule(s2.getId(), RuleDTO.builder().condition("DEFAULT").nextStepId(s3.getId()).priority(1).build());

        log.info("Seeded 'Employee Onboarding' workflow: id={}", wf.getId());
    }
}
