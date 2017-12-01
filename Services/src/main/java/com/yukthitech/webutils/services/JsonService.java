package com.yukthitech.webutils.services;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectMapper.DefaultTyping;
import com.yukthitech.utils.exceptions.InvalidStateException;

/**
 * Service to convert objects into json and from json to object.
 * @author akiran
 */
@Service
public class JsonService
{
	/**
	 * Object mapper to convert objects into json along with type info.
	 */
	private static ObjectMapper objectMapper = new ObjectMapper();
	
	static
	{
		objectMapper.enableDefaultTyping(DefaultTyping.NON_FINAL, As.PROPERTY);
	}

	/**
	 * Converts object to json string.
	 * @param value value to be converted
	 * @return converted json string.
	 */
	public String toJsonString(Object value)
	{
		if(value == null)
		{
			return null;
		}
		
		try
		{
			return objectMapper.writeValueAsString(value);
		}catch(Exception ex)
		{
			throw new InvalidStateException("An error occurred while coverting specified object into json: {}", value, ex);
		}
	}
	
	/**
	 * Parses specified object into json.
	 * @param json json to be parsed.
	 * @return read object
	 */
	public Object parseJson(String json)
	{
		try
		{
			return objectMapper.readValue(json, Object.class);
		}catch(Exception ex)
		{
			throw new InvalidStateException("An error occurred while coverting specified json into object: {}", json, ex);
		}
	}
}
