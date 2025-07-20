package com.yukthitech.webutils.common.verification;

import java.util.function.BiFunction;

import com.yukthitech.webutils.common.ValueWithToken;
import com.yukthitech.webutils.common.annotations.NeedVerification;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * Validator for {@link NeedVerification} annotation.
 */
public class VerificationValidator implements ConstraintValidator<NeedVerification, ValueWithToken>
{
	private static BiFunction<VerificationType, ValueWithToken, Boolean> validatorFunction;
	
	private NeedVerification needVerification;
	
	public static void setValidatorFunction(BiFunction<VerificationType, ValueWithToken, Boolean> validatorFunction)
	{
		VerificationValidator.validatorFunction = validatorFunction;
	}
	
	/* (non-Javadoc)
	 * @see javax.validation.ConstraintValidator#initialize(java.lang.annotation.Annotation)
	 */
	@Override
	public void initialize(NeedVerification needVerification)
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

