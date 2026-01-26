package com.webutils.services.common;

import java.io.File;
import java.io.IOException;
import java.net.URLConnection;
import java.util.Collection;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.yukthitech.utils.exceptions.InvalidStateException;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletResponse;

@Service
public class FileService
{
	@Value("${app.webutils.filesRoot:'./data'}")
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
	
	public String save(String groupName, String filePrefix, MultipartFile multipartFile)
	{
		String fullFileName = filePrefix+ "#"  + multipartFile.getOriginalFilename();

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

			FileUtils.copyInputStreamToFile(multipartFile.getInputStream(), new File(rooDir, fullFileName));
		}catch(Exception ex)
		{
			throw new InvalidStateException("Error in saving file", ex);
		}

		return fullFileName;
	}

	public void writeTo(String groupName, String fullFileName, HttpServletResponse response)
	{
		try
		{
			File rooDir = this.filesRootDir;

			if(StringUtils.isNotBlank(groupName))
			{
				rooDir = new File(rooDir, groupName);
			}

			File file = new File(rooDir, fullFileName);

			if(!file.exists())
			{
				throw new InvalidStateException("File not found: {} [Group: {}]", fullFileName, groupName);
			}
			
			int idx = fullFileName.indexOf('#');
			String fileName = fullFileName.substring(idx + 1);

			response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
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
		}
	}
}
