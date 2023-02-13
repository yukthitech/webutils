package com.yukthitech.webutils.resources;

import java.util.Arrays;

import javax.annotation.PostConstruct;
import javax.servlet.Servlet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.stereotype.Component;

/**
 * Used to register the resource servlet.
 * @author akranthikiran
 */
@Component
public class ResourceServletInit extends ServletRegistrationBean<Servlet>
{
	private static Logger logger = LogManager.getLogger(ResourceServletInit.class);
	
	@Value("${webutils.ui.path}")
	private String uiPath;
	
	@Autowired
	private ResourcesServlet servlet;
	
	@PostConstruct
	private void init()
	{
		logger.debug("Registering the resource servlet with paths: {}", uiPath);
		
		String paths[] = uiPath.trim().split("\\s*\\,\\s*");
		
		super.setServlet(servlet);
		super.setUrlMappings(Arrays.asList(paths));
	}
}
