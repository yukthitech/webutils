package com.yukthitech.webutils.common;

/**
 * Used to specify image information in the model.
 * @author akiran
 */
public class ImageInfo
{
	/**
	 * File id to be used as image.
	 */
	private Long fileId;
	
	/**
	 * Indicates if the current image information is new image or not. For new images,
	 * corresponding file will be converted into permanent file.
	 */
	private boolean newImage;
	
	/**
	 * Instantiates a new image info.
	 */
	public ImageInfo()
	{}
	
	/**
	 * Instantiates a new image info.
	 *
	 * @param fileId the file id
	 */
	public ImageInfo(Long fileId)
	{
		this.fileId = fileId;
		this.newImage = false;
	}

	/**
	 * Gets the file id to be used as image.
	 *
	 * @return the file id to be used as image
	 */
	public Long getFileId()
	{
		return fileId;
	}

	/**
	 * Sets the file id to be used as image.
	 *
	 * @param fileId the new file id to be used as image
	 */
	public void setFileId(Long fileId)
	{
		this.fileId = fileId;
	}

	/**
	 * Gets the indicates if the current image information is new image or not. For new images, corresponding file will be converted into permanent file.
	 *
	 * @return the indicates if the current image information is new image or not
	 */
	public boolean isNewImage()
	{
		return newImage;
	}

	/**
	 * Sets the indicates if the current image information is new image or not. For new images, corresponding file will be converted into permanent file.
	 *
	 * @param newImage the new indicates if the current image information is new image or not
	 */
	public void setNewImage(boolean newImage)
	{
		this.newImage = newImage;
	}
}
