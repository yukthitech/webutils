/*
 * The MIT License (MIT)
 * Copyright (c) 2016 "Yukthi Techsoft Pvt. Ltd." (http://yukthi-tech.co.in)

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

package com.webutils.services.mail;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.RenderableImage;
import java.io.File;
import java.nio.charset.Charset;
import java.util.UUID;

import javax.imageio.ImageIO;

import org.apache.commons.io.FileUtils;

import com.yukthitech.utils.exceptions.InvalidStateException;

import lombok.Data;

/**
 * Represents email file attachment.
 * @author akiran
 */
@Data
public class MailAttachment
{
	/**
	 * File to be attachment.
	 */
	private File fileContent;
	
	/**
	 * Name of the file in email.
	 */
	private String fileName;
	
	/**
	 * Content id to be used for this attachment.
	 */
	private String contentId = UUID.randomUUID().toString();
	
	public void setStringContent(String content)
	{
		try
		{
			this.fileContent = File.createTempFile("string-content", ".tmp");
			FileUtils.write(fileContent, content, Charset.defaultCharset());
		}catch(Exception e)
		{
			throw new InvalidStateException("Error writing string content to file", e);
		}
	}
	
	public void setBinaryContent(byte[] content)
	{
		try
		{
			this.fileContent = File.createTempFile("binary-content", ".tmp");
			FileUtils.writeByteArrayToFile(fileContent, content);
		}catch(Exception e)
		{
			throw new InvalidStateException("Error writing binary content to file", e);
		}
	}
	
	public void setImageContent(Image image)
	{
		try
		{
			this.fileContent = File.createTempFile("image-content", ".tmp");
		
			String imgType = fileName == null || fileName.indexOf('.') <= 0 ? null :
				fileName.substring(fileName.lastIndexOf('.') + 1, fileName.length());
			imgType = (imgType == null) ? "png" : imgType;
			
			if(!(image instanceof RenderableImage))
			{
				Image img = (Image) image;
				BufferedImage bimg = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_4BYTE_ABGR);
				bimg.getGraphics().drawImage(img, 0, 0, null);

				image = bimg;
			}

			ImageIO.write((RenderedImage) image, imgType.toLowerCase(), fileContent);
		}catch(Exception e)
		{
			throw new InvalidStateException("Error writing image content to file", e);
		}
	}
}
