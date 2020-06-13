package com.stb.java.templates;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class TemplateLoader {

    private static final Logger LOGGER = LoggerFactory.getLogger(TemplateLoader.class);
    private static Map<String, String> templates;

    public static void loadTemplate() {
        Field[] fields = Templates.class.getFields();
        Map _templates = new HashMap();
        try {
            for (Field field : fields) {
                String name = (String) field.get(null);
                _templates.put(name,readJson(name));
            }
            templates = Collections.unmodifiableMap(_templates);
        } catch (IllegalAccessException | IOException e) {
            e.printStackTrace();
        }
    }

    public static Object getTemplate(String name){
        return templates.getOrDefault(name,"");
    }

    public static void templates(){
        LOGGER.info(templates.toString());
    }

    private static String readJson(String fileName) throws IOException {
        return new String(Files.readAllBytes(new File("src/main/resources/"+fileName).toPath()));
    }
}
