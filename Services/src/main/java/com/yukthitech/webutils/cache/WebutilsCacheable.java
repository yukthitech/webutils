package com.yukthitech.webutils.cache;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.cache.annotation.Cacheable;

/**
 * Used to mark methods as cacheable with webutils custom way of key generation.
 * @author akiran
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
@Cacheable(keyGenerator = "cahceKeyGenerator")
public @interface WebutilsCacheable
{
	/**
	 * Can be used to specified custom key with SPEL expressions. Defaults to combination of method name and all params.
	 * By default method name is also used along with generated key.
	 * @return spel expression representing key.
	 */
	public String key() default "";
	
	/**
	 * Defines the group under which current data will be cached. This can be used again in eviction. This is SPEL expression.
	 * @return group.
	 */
	public String[] groups() default {};
	
	/**
	 * If true, excludes method name from generated key.
	 * @return true if method name should be excluded.
	 */
	public boolean excludeMethodName() default false;
	
	/**
	 * If true, params will be excluded from generated key.
	 * @return true if params to be excluded.
	 */
	public boolean excludeParams() default false;
}
