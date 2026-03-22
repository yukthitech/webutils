package com.webutils.services.common;

import java.io.File;
import java.io.IOException;
import java.net.URLConnection;
import java.util.Collection;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.webutils.common.FileInfo;
import com.webutils.common.UserDetails;
import com.webutils.services.auth.UserContext;
import com.yukthitech.utils.exceptions.InvalidStateException;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;

@Service
public class FileService
{
	private static ObjectMapper objectMapper = new ObjectMapper();

	@Autowired
	private SecurityService securityService;

	@Value("${app.webutils.filesRoot:./data}")
	private String filesRoot;

	private File filesRootDir;

	@PostConstruct
	private void init() throws IOException
	{
		filesRootDir = new File(filesRoot);

		if(!filesRootDir.exists())
		{
			FileUtils.forceMkdir(filesRootDir);
		}
	}

	public boolean isExistingFile(String groupName, String fileName)
	{
		File rooDir = this.filesRootDir;

		if(StringUtils.isNotBlank(groupName))
		{
			rooDir = new File(rooDir, groupName);
		}

		File file = new File(rooDir, fileName);
		return file.exists();
	}
	
	public String save(String groupName, String filePrefix, Part part)
	{
		String fullFileName = filePrefix+ "#"  + part.getSubmittedFileName();

		try
		{
			File rooDir = this.filesRootDir;

			if(StringUtils.isNotBlank(groupName))
			{
				File groupDir = new File(rooDir, groupName);
				
				if(!groupDir.exists())
				{
					groupDir.mkdirs();
				}

				rooDir = groupDir;
			}

			FileUtils.copyInputStreamToFile(part.getInputStream(), new File(rooDir, fullFileName));

			UserDetails userDetails = UserContext.getCurrentUser();
			FileInfo fileInfo = new FileInfo()
				.setOwnerId(userDetails == null ? null : userDetails.getId())
				.setFileName(fullFileName)
				.setGroupName(groupName)
				;

			File infoFile = new File(rooDir, fullFileName + ".info");
			objectMapper.writeValue(infoFile, fileInfo);
		}catch(Exception ex)
		{
			throw new InvalidStateException("Error in saving file", ex);
		}

		return fullFileName;
	}

	public void writeTo(String groupName, String fullFileName, boolean inline, HttpServletResponse response)
	{
		try
		{
			File rooDir = this.filesRootDir;

			if(StringUtils.isNotBlank(groupName))
			{
				rooDir = new File(rooDir, groupName);
			}
			
			File infoFile = new File(rooDir, fullFileName + ".info");
			File file = new File(rooDir, fullFileName);

			if(!infoFile.exists())
			{
				throw new InvalidStateException("File info not found: {} [Group: {}]", fullFileName, groupName);
			}

			if(!file.exists())
			{
				throw new InvalidStateException("File not found: {} [Group: {}]", fullFileName, groupName);
			}

			FileInfo fileInfo = objectMapper.readValue(infoFile, FileInfo.class);
			securityService.checkFileAuthorization(fileInfo);

			int idx = fullFileName.indexOf('#');
			String fileName = fullFileName.substring(idx + 1);

			response.setHeader("Content-Disposition", (inline ? "inline" : "attachment") + "; filename=\"" + fileName + "\"");
			response.setContentType(URLConnection.guessContentTypeFromName(fileName));
			response.setContentLength((int) file.length());

			FileUtils.copyFile(file, response.getOutputStream());
		}catch(Exception ex)
		{
			throw new InvalidStateException("Error in writing file to response", ex);
		}
	}

	public void delete(String groupName, Collection<String> fileNames)
	{
		if(fileNames == null || fileNames.isEmpty())
		{
			return;
		}
		
		File rooDir = this.filesRootDir;

		if(StringUtils.isNotBlank(groupName))
		{
			rooDir = new File(rooDir, groupName);
		}
		
		for(String fileName : fileNames)
		{
			new File(rooDir, fileName).delete();
			new File(rooDir, fileName + ".info").delete();
		}
	}
}
