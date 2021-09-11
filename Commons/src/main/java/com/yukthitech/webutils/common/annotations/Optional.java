package com.yukthitech.webutils.common.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;

import com.yukthitech.validation.cross.EnableCrossValidation;

/**
 * Marks target element as optional.
 * @author akiran
 */
@EnableCrossValidation
@Constraint(validatedBy = {})
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Optional
{
}
