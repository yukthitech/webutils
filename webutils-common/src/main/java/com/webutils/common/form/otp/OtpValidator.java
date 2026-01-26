package com.webutils.common.form.otp;

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
	 * @see javax.validation.ConstraintValidator#isValid(java.lang.Object, javax.validation.ConstraintValidatorContext)
	 */
	@Override
	public boolean isValid(OtpVerification valueWithToken, ConstraintValidatorContext context)
	{
		try
		{
			validatorFunction.validate(needVerification.type(), valueWithToken);
			return true;
		}catch(Exception ex)
		{
			context.buildConstraintViolationWithTemplate(ex.getMessage())
				.addConstraintViolation();
			
			return false;
		}
	}
}

