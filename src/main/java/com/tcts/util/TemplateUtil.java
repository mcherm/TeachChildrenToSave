package com.tcts.util;

import java.io.IOException;
import java.util.Map;
import java.io.StringWriter;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.tcts.exception.AppConfigurationException;

@Component
public class TemplateUtil {
	
	@Autowired
	private VelocityEngine velocityEngine;
	
	public String generateTemplate(String templateType,Map<String,Object> model) throws IOException, AppConfigurationException {

        if (velocityEngine == null) {
            throw new AppConfigurationException(
                    "Cannot send any emails because the velocity engine is not properly configured.");
        }

        VelocityContext velocityContext = new VelocityContext(model);
        String templateName = "template/"+ templateType + ".vm";
        StringWriter stringWriter = new StringWriter();
        try {
            velocityEngine.mergeTemplate(templateName, "UTF-8", velocityContext, stringWriter);
        } catch(Exception err) {
            throw new IOException("Unable to merge template.", err);
        }
        String text = stringWriter.toString();
        return text;
    }

}
