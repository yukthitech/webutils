package com.yukthitech.webutils.cache;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Objects;
import java.util.Set;

/**
 * Represents key that can be used for caching.
 * @author akiran
 */
public class CacheKey implements Serializable
{
	private static final long serialVersionUID = 1L;

	/**
	 * Name of the method.
	 */
	private String methodName;
	
	/**
	 * Custom key specified.
	 */
	private Object key;
	
	/**
	 * Args used.
	 */
	private Object args[];
	
	/**
	 * Group under which caching should be done.
	 */
	private Set<String> groups;
	
	/**
	 * If set to true, all entries will be removed.
	 */
	private boolean removeAllEntries = false;

	/**
	 * Instantiates a new cache key.
	 *
	 * @param methodName the method name
	 * @param key the key
	 * @param args the args
	 * @param groups the group
	 */
	public CacheKey(String methodName, Object key, Object[] args, Set<String> groups)
	{
		this.methodName = methodName;
		this.key = key;
		this.args = args;
		this.groups = groups;
	}

	/**
	 * Gets the group under which caching should be done.
	 *
	 * @return the group under which caching should be done
	 */
	public Set<String> getGroups()
	{
		return groups;
	}
	
	/**
	 * Sets the if set to true, all entries will be removed.
	 *
	 * @param removeAllEntries the new if set to true, all entries will be removed
	 */
	public void setRemoveAllEntries(boolean removeAllEntries)
	{
		this.removeAllEntries = removeAllEntries;
	}
	
	/**
	 * Checks if is if set to true, all entries will be removed.
	 *
	 * @return the if set to true, all entries will be removed
	 */
	public boolean isRemoveAllEntries()
	{
		return removeAllEntries;
	}

	/**
	 * Hash code.
	 *
	 * @return the int
	 */
	/* (non-Javadoc)
	 * @see java.lang.Object#hashcode()
	 */
	@Override
	public int hashCode()
	{
		int code = methodName != null ? methodName.hashCode() : 0;
		code += key != null ? key.hashCode() : 0;
		
		if(args != null)
		{
			for(Object arg : args)
			{
				code += arg.hashCode();
			}
		}
		
		return code;
	}
	
	/**
	 * Equals.
	 *
	 * @param obj the obj
	 * @return true, if successful
	 */
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj)
	{
		if(obj == this)
		{
			return true;
		}

		if(!(obj instanceof CacheKey))
		{
			return false;
		}

		CacheKey other = (CacheKey) obj;
		
		if(!Objects.equals(methodName, other.methodName))
		{
			return false;
		}
		
		if(!Objects.equals(key, other.key))
		{
			return false;
		}
		
		if(!Arrays.equals(args, other.args))
		{
			return false;
		}
		
		return true;
	}
}
