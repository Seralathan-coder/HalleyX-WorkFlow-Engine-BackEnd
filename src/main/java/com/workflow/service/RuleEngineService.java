package com.workflow.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.workflow.model.Rule;
import com.workflow.repository.RuleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class RuleEngineService {

    private static final Logger log = LoggerFactory.getLogger(RuleEngineService.class);

    private final RuleRepository ruleRepository;
    private final ExpressionParser parser = new SpelExpressionParser();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public RuleEngineService(RuleRepository ruleRepository) {
        this.ruleRepository = ruleRepository;
    }

    public RuleEvaluationResult evaluateRules(String stepId, String dataJson) {
        List<Rule> rules = ruleRepository.findByStepIdOrderByPriority(stepId);

        if (rules.isEmpty()) {
            log.info("[RuleEngine] No rules found for step {}, proceeding to end", stepId);
            return new RuleEvaluationResult(null, "No rules defined — workflow ended", null);
        }

        Map<String, Object> data = parseData(dataJson);

        for (Rule rule : rules) {
            String condition = rule.getCondition();
            log.debug("[RuleEngine] Evaluating rule {}: condition='{}' priority={}", rule.getId(), condition, rule.getPriority());

            try {
                if ("DEFAULT".equalsIgnoreCase(condition.trim())) {
                    log.info("[RuleEngine] DEFAULT rule matched → nextStepId={}", rule.getNextStepId());
                    return new RuleEvaluationResult(
                            rule.getNextStepId(),
                            "DEFAULT rule matched (priority " + rule.getPriority() + ")",
                            rule.getId()
                    );
                }

                boolean result = evaluateCondition(condition, data);
                log.debug("[RuleEngine] Condition '{}' evaluated to: {}", condition, result);

                if (result) {
                    log.info("[RuleEngine] Rule matched: condition='{}' → nextStepId={}", condition, rule.getNextStepId());
                    return new RuleEvaluationResult(
                            rule.getNextStepId(),
                            "Rule matched: " + condition + " (priority " + rule.getPriority() + ")",
                            rule.getId()
                    );
                }

            } catch (Exception e) {
                log.error("[RuleEngine] Error evaluating condition '{}': {}", condition, e.getMessage());
            }
        }

        log.warn("[RuleEngine] No rules matched for step {}", stepId);
        return new RuleEvaluationResult(null, "No rules matched — workflow ended", null);
    }

    private boolean evaluateCondition(String condition, Map<String, Object> data) {
        EvaluationContext context = new StandardEvaluationContext();
        if (data != null) {
            for (Map.Entry<String, Object> entry : data.entrySet()) {
                context.setVariable(entry.getKey(), entry.getValue());
            }
        }
        String spelCondition = transformToSpel(condition, data);
        log.debug("[RuleEngine] SpEL expression: {}", spelCondition);
        Expression expression = parser.parseExpression(spelCondition);
        Boolean result = expression.getValue(context, Boolean.class);
        return Boolean.TRUE.equals(result);
    }

    private String transformToSpel(String condition, Map<String, Object> data) {
        if (data == null) return condition;
        String spel = condition;
        for (String key : data.keySet()) {
            spel = spel.replaceAll("\\b" + key + "\\b", "#" + key);
        }
        return spel;
    }

    private Map<String, Object> parseData(String dataJson) {
        if (dataJson == null || dataJson.isBlank()) return Map.of();
        try {
            return objectMapper.readValue(dataJson, new TypeReference<Map<String, Object>>() {});
        } catch (Exception e) {
            log.error("[RuleEngine] Failed to parse data JSON: {}", e.getMessage());
            return Map.of();
        }
    }

    public record RuleEvaluationResult(String nextStepId, String reason, String matchedRuleId) {}
}
