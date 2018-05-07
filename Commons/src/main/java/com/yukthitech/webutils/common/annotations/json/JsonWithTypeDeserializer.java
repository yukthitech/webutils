package com.yukthitech.webutils.common.annotations.json;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.yukthitech.webutils.common.IWebUtilsCommonConstants;

/**
 * Used to mark a field to be converted to json-with-type string during deserialization.
 * This is helpful when abstract types are used whose actual type is decided at runtime.
 * 
 * @author akiran
 */
public class JsonWithTypeDeserializer extends StdDeserializer<Object>
{
	private static final long serialVersionUID = 1L;

	/**
	 * Instantiates a new json with type deserializer.
	 */
	public JsonWithTypeDeserializer()
	{
		super((Class<?>) null);
	}
	
	@Override
	public Object deserialize(JsonParser parser, DeserializationContext ctxt) throws IOException, JsonProcessingException
	{
		String text = parser.getText();
		return IWebUtilsCommonConstants.OBJECT_MAPPER_WITH_TYPE.readValue(text, Object.class);
	}
}
