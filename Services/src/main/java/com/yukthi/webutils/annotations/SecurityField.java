package com.yukthi.webutils.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.yukthi.webutils.security.UserDetails;

/**
 * Can be used in {@link UserDetails} bean to mark a field as security field. So that field value will
 * be available across authorized requests.
 * 
 * @author akiran
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface SecurityField
{
}
