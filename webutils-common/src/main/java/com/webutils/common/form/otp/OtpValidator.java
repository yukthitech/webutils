package com.webutils.common.form.otp;

import org.apache.commons.lang3.StringUtils;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * Validator for {@link Otp} annotation.
 */
public class OtpValidator implements ConstraintValidator<Otp, OtpVerification>
{
	private static IOtpValidationFunction validatorFunction;
	
	private Otp needVerification;
	
	public static void setValidatorFunction(IOtpValidationFunction validatorFunction)
	{
		OtpValidator.validatorFunction = validatorFunction;
	}
	
	/* (non-Javadoc)
	 * @see javax.validation.ConstraintValidator#initialize(java.lang.annotation.Annotation)
	 */
	@Override
	public void initialize(Otp needVerification)
	{
		this.needVerification = needVerification;
	}
	
	/* (non-Javadoc)
	 * @see javax.validation.ConstraintValidator#isValid(java.lang.Object, jakarta.validation.ConstraintValidatorContext)
	 */
	@Override
	public boolean isValid(OtpVerification valueWithToken, ConstraintValidatorContext context)
	{
		// Optional OTP fields (e.g. email OR mobile) may be null or blank when unused
		if(valueWithToken == null || isBlankOtp(valueWithToken))
		{
			return true;
		}

		try
		{
			validatorFunction.validate(needVerification.type(), valueWithToken);
			return true;
		}catch(Exception ex)
		{
			context.disableDefaultConstraintViolation();
			context.buildConstraintViolationWithTemplate(ex.getMessage() != null ? ex.getMessage() : needVerification.message())
				.addConstraintViolation();
			
			return false;
		}
	}

	private static boolean isBlankOtp(OtpVerification otp)
	{
		return StringUtils.isBlank(otp.getToken()) && StringUtils.isBlank(otp.getValue());
	}
}
