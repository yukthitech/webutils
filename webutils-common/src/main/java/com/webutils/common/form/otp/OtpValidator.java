package com.webutils.common.form.otp;

import java.util.function.BiFunction;

import com.webutils.common.ValueWithToken;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * Validator for {@link Otp} annotation.
 */
public class OtpValidator implements ConstraintValidator<Otp, ValueWithToken>
{
	private static BiFunction<VerificationType, ValueWithToken, Boolean> validatorFunction;
	
	private Otp needVerification;
	
	public static void setValidatorFunction(BiFunction<VerificationType, ValueWithToken, Boolean> validatorFunction)
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
	public boolean isValid(ValueWithToken valueWithToken, ConstraintValidatorContext context)
	{
		return validatorFunction.apply(needVerification.type(), valueWithToken);
	}
}

