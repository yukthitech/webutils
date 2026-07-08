package com.webutils.services.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;

import org.apache.commons.io.IOUtils;

import jakarta.servlet.http.HttpServletResponse;

/**
 * Streams a temporary file to the HTTP response.
 */
public final class AttachmentDownloadHelper
{
	public static final String MIME_MS_EXCEL_FILE = "application/vnd.ms-excel";

	private AttachmentDownloadHelper()
	{
	}

	public static void sendFile(HttpServletResponse response, String fileName, File file, String mimeType, boolean deleteFile)
	{
		response.setContentType(mimeType);
		response.setContentLengthLong(file.length());
		response.setHeader("Content-Disposition", String.format("attachment; filename=\"%s\"", fileName));

		try(OutputStream os = response.getOutputStream();
				FileInputStream fis = new FileInputStream(file))
		{
			IOUtils.copy(fis, os);
		}
		catch(Exception ex)
		{
			throw new IllegalStateException("An error occurred while sending file - " + file.getPath(), ex);
		}
		finally
		{
			if(deleteFile)
			{
				file.delete();
			}
		}
	}
}
