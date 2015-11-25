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

package com.yukthi.webutils.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.IOUtils;
import org.apache.tika.Tika;

import com.yukthi.webutils.FileDetails;

/**
 * Utils related to web attachments upload and download
 * @author akiran
 */
public class WebAttachmentUtils
{
	public static final String MIME_BINARY_FILE = "application/octet-stream";
	public static final String MIME_PDF_FILE = "application/pdf";
	public static final String MIME_CSV_FILE = "text/csv";
	public static final String MIME_JAR_FILE = "application/java-archive";
	public static final String MIME_JSON_FILE = "application/json";
	public static final String MIME_JPEG_FILE = "image/jpeg";
	
	public static final String MIME_MS_ACCSS_FILE = "application/x-msaccess";
	public static final String MIME_MS_EXCEL_FILE = "application/vnd.ms-excel";
	public static final String MIME_MS_WORD_FILE = "application/msword";
	
	public static final String MIME_ZIP_FILE = "application/zip";
	
	public static final String EXTENSION_MS_EXCEL_FILE = ".xls";
	
	private static final Tika tika = new Tika();

	/**
	* Sends specified content as attachment on response
	*
	* @param response
	* @param mimeType
	* @param fileName
	* @param content
	*/
	public static void sendAttachment(HttpServletResponse response, String mimeType, String fileName, File content)
	{
		if(mimeType == null)
		{
			try
			{
				// set to binary type if MIME mapping not found
				mimeType = tika.detect(content);
			}catch(Exception ex)
			{
				throw new IllegalStateException("An error occurred while fetching file's mime type - " + content.getPath(), ex);
			}
		}

		// modifies response
		response.setContentType(mimeType);
		response.setContentLength((int)content.length());

		// indicator for download
		String headerKey = "Content-Disposition";
		String headerValue = String.format("attachment; filename=\"%s\"", fileName);
		response.setHeader(headerKey, headerValue);
		
		//write content to response
		try
		{
			OutputStream os = response.getOutputStream();
			FileInputStream fis = new FileInputStream(content);
			IOUtils.copy(fis, os);
			
			os.close();
			fis.close();
		}catch(Exception ex)
		{
			throw new IllegalStateException("An error occurred while writing content to response", ex);
		}
	}
	
	/**
	 * Receives the file attachments part of requests and returns file path list
	 * @param request Request from which file attachments needs to be fetched
	 * @return Map of file details, using input file filed name as key
	 */
	public static Map<String, FileDetails> recieveImports(HttpServletRequest request)
	{
		String tempDir = System.getProperty("java.io.tmpdir");
		DiskFileItemFactory factory = new DiskFileItemFactory(0, new File(tempDir));

		// Create a new file upload handler
		ServletFileUpload upload = new ServletFileUpload(factory);

		// Parse the request
		List<FileItem> items = null;
		
		try
		{
			items = upload.parseRequest(request);
		}catch(FileUploadException ex)
		{
			throw new IllegalStateException("An error occurred while receiving uploaded files", ex);
		}
		
		if(items == null || items.isEmpty())
		{
			return null;
		}
		
		Map<String, FileDetails> uploadedFiles = new HashMap<>();
		FileDetails fileDetails = null;
		File file = null;
		
		for(FileItem item: items)
		{
			file = new File( ((DiskFileItem)item).getStoreLocation().getAbsolutePath() );
			fileDetails = new FileDetails(item.getName(), file, item.getContentType(), null, 0L);
			
			uploadedFiles.put(item.getFieldName(), fileDetails);
		}
		
		return uploadedFiles;
	}

}