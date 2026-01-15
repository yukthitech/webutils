package com.webutils.common.captcha;

/**
 * Generated captcha details.
 */
public class CaptchaResponse
{
	/**
	 * Image in base 64 encoding.
	 */
	private String imageBase64;

	/**
	 * Token with encrypted answer.
	 */
	private String token;
	
	/**
	 * 
	 *
	 * @param imageBase64
	 * @param token
	 */
	public CaptchaResponse(String imageBase64, String token)
	{
		this.imageBase64 = imageBase64;
		this.token = token;
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
	 * Gets the token with encrypted answer.
	 *
	 * @return the token with encrypted answer
	 */
	public String getToken()
	{
		return token;
	}

	/**
	 * Sets the token with encrypted answer.
	 *
	 * @param token the new token with encrypted answer
	 */
	public void setToken(String token)
	{
		this.token = token;
	}
}
