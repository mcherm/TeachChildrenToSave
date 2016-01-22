package com.tcts.util;

import java.io.IOException;
import java.util.Map;

import org.apache.velocity.app.VelocityEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ui.velocity.VelocityEngineUtils;

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
        String text = 	VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, "template/"+ templateType + ".vm","UTF-8", model);
        return text;
        
    }

}
