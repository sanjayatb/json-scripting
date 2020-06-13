package com.stb.java.rules;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Option;
import com.stb.java.templates.TemplateLoader;
import net.minidev.json.JSONArray;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashMap;
import java.util.Map;


public class RuleEvaluator {

    private static final Configuration configuration;
    private static final Logger LOGGER = LoggerFactory.getLogger(RuleEvaluator.class);
    private static ObjectMapper jsonParser = new ObjectMapper();
    static {
        configuration = Configuration.defaultConfiguration().addOptions(Option.SUPPRESS_EXCEPTIONS);
    }

    private RuleEvaluator(){
        throw new IllegalStateException("Can't initiate RuleEvaluator");
    }

    public static DocumentContext getJsonDocument(String json){
        return JsonPath.using(configuration).parse(json);
    }

    public static Map getMapForTemplate(String templateName){
        String template = (String) TemplateLoader.getTemplate(templateName);
        if(StringUtils.isBlank(template)){
            throw new IllegalArgumentException("No matching template");
        }
        return getJsonDocument(template).json();
    }

    public static void evaluate(Object input,DocumentContext referenceData,int stackCoutner){

        if(stackCoutner > 200){
            LOGGER.error("Stack Exhausted. Too many nested field. Fail to evaluate all the fields");
            return;
        }

        if(input instanceof DocumentContext){
            evaluate(((DocumentContext) input).json(),referenceData,stackCoutner++);
        }

        if( input instanceof JSONArray){
            for (Object value : ((JSONArray) input).toArray()){
                evaluate(value,referenceData,stackCoutner++);
            }
        }

        if ( input instanceof LinkedHashMap){
            for (Object entry:((LinkedHashMap) input).entrySet()){
                Map.Entry mapEntry = (Map.Entry) entry;
                String keyNow = (String) mapEntry.getKey();
                if(mapEntry.getValue() instanceof String){
                    String value = (String) mapEntry.getValue();
                    Object valueNow = RuleEngine.getRuleEvaluatedValue(value,referenceData);
                    LOGGER.info("Evaluating [{}] field -> returns = [{}]",value,valueNow);
                    ((LinkedHashMap) input).put(keyNow,valueNow);
                }else {
                    evaluate(mapEntry.getValue(),referenceData,stackCoutner++);
                }
            }
        }
    }

    public static String getJson(Object input){
        try {
            return jsonParser.writerWithDefaultPrettyPrinter().writeValueAsString(input);
        } catch (JsonProcessingException e) {
            LOGGER.error("Fail to convert to String",e);
        }
        return "";
    }

}
