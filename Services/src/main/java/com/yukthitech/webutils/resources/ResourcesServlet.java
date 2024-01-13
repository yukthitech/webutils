package com.yukthitech.webutils.resources;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Servlet to serve static resources directly outside spring. So spring
 * apis can be accessed under common path and under common interceptor.
 * 
 * Note: As cache is mentioned as private, the resources will be cached only at browser.
 * 
 * Future Enhancements:
 * 	In prop accept what app paths. And for app-paths the cache time can be decreased for
 *  short time and for non-app-path time can be increased for very long time.
 * @author akranthikiran
 */
@Component
public class ResourcesServlet extends HttpServlet
{
	private static final long serialVersionUID = 1L;
	
	private static Logger logger = LogManager.getLogger(ResourcesServlet.class);

	@Value("${webutils.ui.resourcesFolder}")
	private String resourcesFolderPath;

	@Value("${webutils.ui.welcomePage:index.html}")
	private String welcomePage;

	private File resourcesFolder;
	
	@Value("${webutils.ui.clientCache.disabled:false}")
	private boolean clientCacheDisabled = false;
	
	@Value("${webutils.ui.clientCache.cacheTimeInSec:3600}")
	private long cacheTimeSec;

	@PostConstruct
	private void initConfig()
	{
		this.resourcesFolder = new File(resourcesFolderPath);
		
		logger.debug("Configured resource servlet to serve static resources with: [Folder: {}, Cliet cache disabled: {}, Cahce Time: {}]", 
				resourcesFolderPath, clientCacheDisabled, cacheTimeSec);
	}

	@Override
	protected long getLastModified(HttpServletRequest req)
	{
		String path = req.getRequestURI();
		
		if(StringUtils.isBlank(path) || "/".equals(path.trim()))
		{
			path = welcomePage;
		}
		
		File file = new File(resourcesFolder, path);
		
		if(!file.exists())
		{
			return -1;
		}
		
		return file.lastModified();
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
	{
		String path = req.getRequestURI();
		
		if(StringUtils.isBlank(path) || "/".equals(path.trim()))
		{
			path = welcomePage;
		}
		
		File file = new File(resourcesFolder, path);
		
		if(!file.exists())
		{
			resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Resource not found: " + path);
			return;
		}
		
		ServletContext ctx = getServletContext();
		String mimeType = ctx.getMimeType(file.getAbsolutePath());
		resp.setContentType(mimeType != null? mimeType:"application/octet-stream");
		resp.setContentLength((int) file.length());
		
		long lastMod = file.lastModified();
		resp.addDateHeader("Last-Modified", lastMod);
		resp.addHeader("ETag", "\"" + Long.toHexString(lastMod) + "\"");
		
		if(!clientCacheDisabled)
		{
			resp.addHeader("Cache-Control", String.format("private,max-age=%s,must-revalidate", cacheTimeSec));
		}
		
		FileInputStream fis = new FileInputStream(file);
		OutputStream os = resp.getOutputStream();
		IOUtils.copy(fis, os);
		os.flush();
		os.close();
		fis.close();
	}
}
