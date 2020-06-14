package com.stb.java;

import com.stb.java.rules.RuleEngine;
import com.stb.java.rules.RuleEvaluator;
import com.stb.java.templates.TemplateLoader;
import com.stb.java.templates.Templates;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class RuleTestMain {

    private static final Logger LOGGER = LoggerFactory.getLogger(RuleTestMain.class);

    public static void main(String[] args) {
        RuleEngine.loadRules();
        RuleEngine.rules();
        TemplateLoader.loadTemplate();
        TemplateLoader.templates();

        LOGGER.info("===================================");

        String inputJson = (String) TemplateLoader.getTemplate(Templates.SIMPLE_SCRIPT);
        Map input = RuleEvaluator.getMapForTemplate(Templates.SIMPLE_SCRIPT);

        LOGGER.info("Before Json : {}",inputJson);
        RuleEvaluator.evaluate(input,null,0);

        String outputJson = RuleEvaluator.getJson(input);
        LOGGER.info("After Json : {}",outputJson);
        LOGGER.info("===================================");
    }
}
