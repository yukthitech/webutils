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
	private static Function<ValueWithToken, Boolean> validatorFunction;
	
	public static void setValidatorFunction(Function<ValueWithToken, Boolean> validatorFunction)
	{
		CaptchaValidator.validatorFunction = validatorFunction;
	}
	
	/* (non-Javadoc)
	 * @see javax.validation.ConstraintValidator#initialize(java.lang.annotation.Annotation)
	 */
	@Override
	public void initialize(Captcha matchWith)
	{
	}
	
	/* (non-Javadoc)
	 * @see javax.validation.ConstraintValidator#isValid(java.lang.Object, javax.validation.ConstraintValidatorContext)
	 */
	@Override
	public boolean isValid(ValueWithToken valueWithToken, ConstraintValidatorContext context)
	{
		return validatorFunction.apply(valueWithToken);
	}
}

