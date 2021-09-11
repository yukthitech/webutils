package com.yukthitech.webutils;

import java.util.AbstractMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;

import com.yukthitech.utils.exceptions.InvalidStateException;

/**
 * Placeholder extension to access properties from files, system prop and env variables.
 * @author akiran
 */
public class WebutilsPropertyPlaceholder extends PropertyPlaceholderConfigurer
{
	private AbstractMap<String, String> systemMap = new AbstractMap<String, String>() 
	{
		@Override
		public String get(Object key)
		{
			return System.getProperty(key.toString());
		}
		
		@Override
		public Set<Entry<String, String>> entrySet()
		{
			return null;
		}
	};

	private Properties envProp;
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Map<String, String> getEnv()
	{
		if(envProp != null)
		{
			return (Map) envProp;
		}
		
		try
		{
			Properties prop = new Properties();
			super.loadProperties(prop);
			
			this.envProp = prop;
			return (Map) prop;
		}catch(Exception ex)
		{
			throw new InvalidStateException("An error occurred while loading spring properties", ex);
		}
	}
	
	public AbstractMap<String, String> getSystem()
	{
		return systemMap;
	}
}
