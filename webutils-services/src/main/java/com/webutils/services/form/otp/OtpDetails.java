package com.webutils.services.form.otp;

import com.webutils.common.UserDetails;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class OtpDetails
{
	private String target;
	
	private String otp;
	
	private UserDetails userDetails;
}
