package com.webutils.common.form.captcha;

import com.webutils.common.response.BaseResponse;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Generated captcha details.
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class CaptchaResponse extends BaseResponse
{
	/**
	 * Image in base 64 encoding.
	 */
	private String imageBase64;

	/**
	 * Token with encrypted answer.
	 */
	private String token;
}
