package com.webutils.common;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.yukthitech.validation.cross.EnableCrossValidation;

import jakarta.validation.Constraint;

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
