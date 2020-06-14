package com.stb.java.rules;

import com.jayway.jsonpath.DocumentContext;
import com.stb.java.functions.TriFunction;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

import static com.stb.java.rules.RuleSyntax.*;

public class RuleEngine {

    private static final Logger LOGGER = LoggerFactory.getLogger(RuleEngine.class);
    private static Map<String, Object> ruleBook;

    public static void loadRules() {
        Field[] fields = Rules.class.getFields();
        Map rules = new HashMap();
        try {
            for (Field field : fields) {
                rules.put(field.getName(), field.get(null));
            }
            ruleBook = Collections.unmodifiableMap(rules);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public static Object getRule(String name) {
        if (ruleBook.containsKey(name))
            return ruleBook.get(name);
        throw new IllegalArgumentException(name+" Rule Not Found");
    }

    public static void rules() {
        LOGGER.info(ruleBook.keySet().toString());
    }

    public static Object getRuleEvaluatedValue(String ruleText, DocumentContext referenceData) {
        if (StringUtils.isBlank(ruleText)) {
            return ruleText;
        }

        if (ruleText.startsWith(RuleSyntax.OBJECT_ACCESS)) {
            try {
                return null != referenceData ? referenceData.read(ruleText) : RULE_FAIL_DEFAULT_VALUE;
            } catch (Exception e) {
                LOGGER.error("Fail to find the field in reference data");
            }
            return RULE_FAIL_DEFAULT_VALUE;
        }

        if (ruleText.startsWith(RuleSyntax.SUPPLIER)) {
            return ((Supplier<Object>) getRule(ruleText)).get();
        }

        if (ruleText.startsWith(RuleSyntax.FUNCTION)) {
            String[] parts = ruleText.split(FUNCTION_START);
            if (parts.length != 2) {
                throw new IllegalArgumentException("Invalid Function signature. " + ruleText);
            }
            String parameters = parts[1].replaceAll(FUNCTION_END, "");
            String[] params = parameters.split(",");
            if (params.length != 1) {
                throw new IllegalArgumentException("Invalid Parameters" + parameters);
            }
            String param1 = (String) getRuleEvaluatedValue(params[0], referenceData);
            return ((Function<Object, Object>) getRule(parts[0])).apply(param1);
        }

        if (ruleText.startsWith(BIFUNCTION)) {
            String[] parts = ruleText.split(FUNCTION_START);
            if (parts.length != 2) {
                throw new IllegalArgumentException("Invalid BiFunction signature. " + ruleText);
            }
            String parameters = parts[1].replaceAll(FUNCTION_END, "");
            String[] params = parameters.split(",");
            if (params.length != 2) {
                throw new IllegalArgumentException("Invalid Parameters" + parameters);
            }
            String param1 = (String) getRuleEvaluatedValue(params[0], referenceData);
            String param2 = (String) getRuleEvaluatedValue(params[1], referenceData);
            return ((BiFunction<Object, Object, Object>) getRule(parts[0])).apply(param1, param2);
        }

        if (ruleText.startsWith(TRIFUNCTION)) {
            String[] parts = ruleText.split(FUNCTION_START);
            if (parts.length != 2) {
                throw new IllegalArgumentException("Invalid TriFunction signature. " + ruleText);
            }
            String parameters = parts[1].replaceAll(FUNCTION_END, "");
            String[] params = parameters.split(",");
            if (params.length != 3) {
                throw new IllegalArgumentException("Invalid Parameters" + parameters);
            }
            String param1 = (String) getRuleEvaluatedValue(params[0], referenceData);
            String param2 = (String) getRuleEvaluatedValue(params[1], referenceData);
            String param3 = (String) getRuleEvaluatedValue(params[2], referenceData);
            return ((TriFunction<Object, Object, Object, Object>) getRule(parts[0])).apply(param1, param2, param3);
        }
        return ruleText;
    }
}
