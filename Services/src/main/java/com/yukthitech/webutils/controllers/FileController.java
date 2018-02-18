/*
 * The MIT License (MIT)
 * Copyright (c) 2015 "Yukthi Techsoft Pvt. Ltd." (http://yukthi-tech.co.in)

 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.yukthitech.webutils.controllers;

import static com.yukthitech.webutils.common.IWebUtilsActionConstants.ACTION_PREFIX_FILES;
import static com.yukthitech.webutils.common.IWebUtilsActionConstants.ACTION_TYPE_FETCH;
import static com.yukthitech.webutils.common.IWebUtilsActionConstants.ACTION_TYPE_FETCH_ATTACHMENT;
import static com.yukthitech.webutils.common.IWebUtilsActionConstants.ACTION_TYPE_INSECURE;
import static com.yukthitech.webutils.common.IWebUtilsActionConstants.ACTION_TYPE_UPLOAD;
import static com.yukthitech.webutils.common.IWebUtilsActionConstants.PARAM_ID;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.UUID;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.sax.ToXMLContentHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.yukthitech.webutils.annotations.ActionName;
import com.yukthitech.webutils.annotations.AttachmentsExpected;
import com.yukthitech.webutils.annotations.NoAuthentication;
import com.yukthitech.webutils.common.FileInfo;
import com.yukthitech.webutils.common.IWebUtilsCommonConstants;
import com.yukthitech.webutils.common.models.BasicSaveResponse;
import com.yukthitech.webutils.common.models.FileReadResponse;
import com.yukthitech.webutils.common.models.UploadTempFileRequest;
import com.yukthitech.webutils.repository.file.FileEntity;
import com.yukthitech.webutils.security.ISecurityService;
import com.yukthitech.webutils.security.UnauthorizedException;
import com.yukthitech.webutils.services.FileService;
import com.yukthitech.webutils.utils.WebAttachmentUtils;
import com.yukthitech.webutils.utils.WebUtils;

/**
 * Controller for fetching files.
 * @author akiran
 */
@RestController
@ActionName(ACTION_PREFIX_FILES)
@RequestMapping("/files")
public class FileController
{
	/**
	 * File service to fetch file content based on id.
	 */
	@Autowired
	private FileService fileService;
	
	/**
	 * Service to check authorization for file.
	 */
	@Autowired
	private ISecurityService securityService;
	
	/**
	 * Current http servlet response.
	 */
	@Autowired
	private HttpServletResponse response;
	
	/**
	 * Controller method to upload file.
	 * @param file File information to save
	 * @param request Multi part request required for file upload
	 * @return Return save response with file id.
	 */
	@AttachmentsExpected
	@ActionName(ACTION_TYPE_UPLOAD)
	@RequestMapping(value = "/upload/temp", method = RequestMethod.POST)
	public BasicSaveResponse uploadTempFile(@RequestPart(IWebUtilsCommonConstants.MULTIPART_DEFAULT_PART) @Valid UploadTempFileRequest file, 
		MultipartHttpServletRequest request)
	{
		long id = fileService.saveFileForOwner(file.getFile(), Object.class, UUID.randomUUID().toString(), 0);
		return new BasicSaveResponse(id);
	}
	
	/**
	 * @param id
	 * @param response
	 * @return
	 * @throws IOException
	 */
	private FileInfo getFileInfo(long id, boolean secured, HttpServletResponse response) throws IOException
	{
		FileEntity fileEntity = null;
		
		if(secured)
		{
			fileEntity = fileService.getFileEntity(id);
		}
		else
		{
			fileEntity = fileService.getFileEntity(id, false);
		}
		
		//if file is not found
		if(fileEntity == null)
		{
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
			return null;
		}
		
		//check authorization of current user
		if(secured && !securityService.isAuthorized(fileEntity))
		{
			//delete the temp file that was created during db read
			fileEntity.getFile().delete();

			throw new UnauthorizedException("Current user is not authorized to access file with id - {}", id);
		}
		
		FileInfo fileInfo = WebUtils.convertBean(fileEntity, FileInfo.class);
		fileInfo.setFile(fileEntity.getFile());
		
		return fileInfo;
	}

	/**
	 * Fetches file content from db for specified id, as part of request body. Useful to include content as
	 * image, css etc
	 * @param id Id of the file to be fetched
	 */
	@ActionName(ACTION_TYPE_FETCH)
	@RequestMapping(value = "/fetch/{" + PARAM_ID + "}", method = RequestMethod.GET)
	public void fetchFile(@PathVariable(PARAM_ID) long id) throws IOException
	{
		FileInfo fileInfo = getFileInfo(id, true, response);

		if(fileInfo == null)
		{
			return;
		}

		WebAttachmentUtils.sendFile(response, fileInfo, false, true);
	}

	/**
	 * Fetches file content from db for specified id, as part attachment.
	 * @param id Id of the file to be fetched
	 */
	@ActionName(ACTION_TYPE_FETCH_ATTACHMENT)
	@RequestMapping(value = "/download/{" + PARAM_ID + "}", method = RequestMethod.GET)
	public void fetchFileAsAttachment(@PathVariable(PARAM_ID) long id) throws IOException
	{
		FileInfo fileInfo = getFileInfo(id, true, response);

		if(fileInfo == null)
		{
			return;
		}

		WebAttachmentUtils.sendFile(response, fileInfo, true, true);
	}

	/**
	 * Fetches file content from db for specified id, as part of request body. Useful to include content as
	 * image, css etc
	 * @param id Id of the file to be fetched
	 */
	@NoAuthentication
	@ActionName(ACTION_TYPE_FETCH + "." + ACTION_TYPE_INSECURE)
	@RequestMapping(value = "/fetch/insecure/{" + PARAM_ID + "}", method = RequestMethod.GET)
	public void fetchInsecureFile(@PathVariable(PARAM_ID) long id) throws IOException
	{
		FileInfo fileInfo = getFileInfo(id, false, response);

		if(fileInfo == null)
		{
			return;
		}

		WebAttachmentUtils.sendFile(response, fileInfo, false, true);
	}

	/**
	 * Fetches file content from db for specified id, as part attachment.
	 * @param id Id of the file to be fetched
	 */
	@NoAuthentication
	@ActionName(ACTION_TYPE_FETCH_ATTACHMENT + "." + ACTION_TYPE_INSECURE)
	@RequestMapping(value = "/download/insecure/{" + PARAM_ID + "}", method = RequestMethod.GET)
	public void fetchInsecureFileAsAttachment(@PathVariable(PARAM_ID) long id) throws IOException
	{
		FileInfo fileInfo = getFileInfo(id, false, response);

		if(fileInfo == null)
		{
			return;
		}

		WebAttachmentUtils.sendFile(response, fileInfo, true, true);
	}
	
	/**
	 * API to read content as html.
	 * @param id id of file to read
	 * @return file content in html format. 
	 */
	@ActionName("contentAsHtml")
	@RequestMapping(value = "/contentAsHtml/{" + PARAM_ID + "}", method = RequestMethod.GET)
	public FileReadResponse getFileContentAsHtml(@PathVariable(PARAM_ID) long id) throws Exception
	{
		FileInfo fileInfo = getFileInfo(id, false, response);

		if(fileInfo == null)
		{
			return new FileReadResponse("No file available with specified id: " + id);
		}

		ToXMLContentHandler handler = new ToXMLContentHandler();
		AutoDetectParser parser = new AutoDetectParser();
		Metadata metadata = new Metadata();
		
		File file = fileInfo.getFile();
		
		//rename the file
		File renamedFile = File.createTempFile(FilenameUtils.getBaseName(fileInfo.getFileName()), "." + FilenameUtils.getExtension(fileInfo.getFileName()));
		FileUtils.forceDelete(renamedFile);
		FileUtils.moveFile(file, renamedFile);
		file.delete();

		FileInputStream fis = new FileInputStream(renamedFile);
		parser.parse(fis, handler, metadata);
		String resultContent = handler.toString();
		
		fis.close();
		return new FileReadResponse(resultContent);
	}
}
