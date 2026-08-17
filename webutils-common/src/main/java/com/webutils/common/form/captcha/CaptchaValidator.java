package com.webutils.common.form.captcha;

import java.util.function.Function;

import com.webutils.common.ValueWithToken;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * Validator for captcha annotation.
 */
public class CaptchaValidator implements ConstraintValidator<Captcha, ValueWithToken>
{
	private static Function<ValueWithToken, CaptchaValidationResult> validatorFunction;

	private String message;

	private String expiredMessage;
	
	public static void setValidatorFunction(Function<ValueWithToken, CaptchaValidationResult> validatorFunction)
	{
		CaptchaValidator.validatorFunction = validatorFunction;
	}
	
	/* (non-Javadoc)
	 * @see javax.validation.ConstraintValidator#initialize(java.lang.annotation.Annotation)
	 */
	@Override
	public void initialize(Captcha captcha)
	{
		this.message = captcha.message();
		this.expiredMessage = captcha.expiredMessage();
	}
	
	/* (non-Javadoc)
	 * @see javax.validation.ConstraintValidator#isValid(java.lang.Object, jakarta.validation.ConstraintValidatorContext)
	 */
	@Override
	public boolean isValid(ValueWithToken valueWithToken, ConstraintValidatorContext context)
	{
		CaptchaValidationResult result = validatorFunction.apply(valueWithToken);

		if(result == null || result == CaptchaValidationResult.VALID)
		{
			return true;
		}

		String template = (result == CaptchaValidationResult.EXPIRED) ? expiredMessage : message;
		context.disableDefaultConstraintViolation();
		context.buildConstraintViolationWithTemplate(template)
			.addConstraintViolation();
		return false;
	}
}
