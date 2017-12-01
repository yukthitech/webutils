package com.yukthitech.webutils.repository;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yukthitech.webutils.security.ISecurityService;

/**
 * Context that will be set on all repositories during loading.
 * 
 * @author akiran
 */
@Service
public class RepositoryContext
{
	/**
	 * Security service to fetch user space identity.
	 */
	@Autowired
	private ISecurityService securityService;
	
	/**
	 * Dynamic parameters.
	 */
	private Map<String, Object> params = new HashMap<>();
	
	/**
	 * Fetches current user space identity.
	 * @return current user space identity
	 */
	public String getUserSpaceIdentity()
	{
		return securityService.getUserSpaceIdentity();
	}
	
	/**
	 * Adds specified param to underlying map.
	 * @param name Name of the param
	 * @param value Value of the param
	 */
	public void addParam(String name, Object value)
	{
		params.put(name, value);
	}
	
	/**
	 * Fetches current params from this context.
	 * @return params
	 */
	public Map<String, Object> getParams()
	{
		return params;
	}
}
