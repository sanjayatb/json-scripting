package com.stb.java.rules;

import com.jayway.jsonpath.DocumentContext;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import static com.stb.java.rules.RuleSyntax.RULE_FAIL_DEFAULT_VALUE;

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

    public static Object getRule(String name){
        return ruleBook.getOrDefault(name,new Object());
    }

    public static void rules(){
        LOGGER.info(ruleBook.keySet().toString());
    }

    public static Object getRuleEvaluatedValue(String ruleText, DocumentContext referenceData) {
        if(StringUtils.isBlank(ruleText)){
            return ruleText;
        }

        if(ruleText.startsWith(RuleSyntax.OBJECT_ACCESS)){
            try {
                return null!=referenceData?referenceData.read(ruleText):RULE_FAIL_DEFAULT_VALUE;
            }catch (Exception e){
                LOGGER.error("Fail to find the field in reference data");
            }
            return RULE_FAIL_DEFAULT_VALUE;
        }

        if(ruleText.startsWith(RuleSyntax.SUPPLIER)){
            return ((Supplier<Object>) getRule(ruleText)).get();
        }

        return ruleText;
    }
}
