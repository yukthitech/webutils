package com.webutils.common.form.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Used to mark a method as a parent provider for a LOV.
 * @author kranthi
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface LovParentProvider 
{
    /**
     * Name of the parent provider.
     * @return name of the parent provider
     */
    public String name();
}
