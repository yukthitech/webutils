package com.yukthitech.webutils.common.captcha;

import com.yukthitech.webutils.common.models.BaseResponse;

/**
 * Generated captcha details.
 */
public class CaptchaResponse extends BaseResponse
{
	/**
	 * Image in base 64 encoding.
	 */
	private String imageBase64;

	/**
	 * Encrypted answer.
	 */
	private String encAns;
	
	public CaptchaResponse(String imageBase64, String encAns)
	{
		this.imageBase64 = imageBase64;
		this.encAns = encAns;
	}

	/**
	 * Gets the image in base 64 encoding.
	 *
	 * @return the image in base 64 encoding
	 */
	public String getImageBase64()
	{
		return imageBase64;
	}

	/**
	 * Sets the image in base 64 encoding.
	 *
	 * @param imageBase64 the new image in base 64 encoding
	 */
	public void setImageBase64(String imageBase64)
	{
		this.imageBase64 = imageBase64;
	}

	/**
	 * Gets the encrypted answer.
	 *
	 * @return the encrypted answer
	 */
	public String getEncAns()
	{
		return encAns;
	}

	/**
	 * Sets the encrypted answer.
	 *
	 * @param encAns the new encrypted answer
	 */
	public void setEncAns(String encAns)
	{
		this.encAns = encAns;
	}

}
