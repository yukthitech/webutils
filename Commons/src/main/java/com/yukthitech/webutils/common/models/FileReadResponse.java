package com.yukthitech.webutils.common.models;

/**
 * File content response.
 * @author akiran
 */
public class FileReadResponse extends BaseResponse
{
	/**
	 * Content from response.
	 */
	private String content;
	
	/**
	 * Instantiates a new file read response.
	 */
	public FileReadResponse()
	{}

	/**
	 * Instantiates a new file read response.
	 *
	 * @param content the content
	 */
	public FileReadResponse(String content)
	{
		this.content = content;
	}

	/**
	 * Gets the content from response.
	 *
	 * @return the content from response
	 */
	public String getContent()
	{
		return content;
	}

	/**
	 * Sets the content from response.
	 *
	 * @param content the new content from response
	 */
	public void setContent(String content)
	{
		this.content = content;
	}
}
