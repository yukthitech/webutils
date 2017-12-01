package com.yukthitech.webutils.common.models;

import com.yukthitech.webutils.common.FileInfo;
import com.yukthitech.webutils.common.annotations.Model;

/**
 * Request to upload a temp file.
 * 
 * @author akiran
 */
@Model
public class UploadTempFileRequest
{
	/**
	 * File to upload.
	 */
	private FileInfo file;
	
	/**
	 * Gets the file to upload.
	 *
	 * @return the file to upload
	 */
	public FileInfo getFile()
	{
		return file;
	}

	/**
	 * Sets the file to upload.
	 *
	 * @param file the new file to upload
	 */
	public void setFile(FileInfo file)
	{
		this.file = file;
	}
}
