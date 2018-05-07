package com.yukthitech.webutils.cache;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.cache.annotation.CacheEvict;

/**
 * Used to mark methods as cache evication methods.
 * @author akiran
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
@CacheEvict(keyGenerator = "cahceEvictKeyGenerator", cacheResolver = "cacheResolver")
public @interface WebutilsCacheEvict
{
	/**
	 * Can be used to specified custom key with SPEL expressions. Defaults to combination of method name and all params.
	 * By default method name is also used along with generated key.
	 * 
	 * Either of group or key is mandatory. If group is specified, key will not be used.
	 * 
	 * @return spel expression representing key.
	 */
	public String cacheKey() default "";
	
	/**
	 * Defines the group under which current data will be cached. This can be used again in eviction. This is SPEL expression.
	 * 
 	 * Either of group or key is mandatory. If group is specified, key will not be used.
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
	
	/**
	 * If set to true, all entries will be cleaned in target cache.
	 * @return true if all entries needs to be removed
	 */
	public boolean allEntries() default false;
}
