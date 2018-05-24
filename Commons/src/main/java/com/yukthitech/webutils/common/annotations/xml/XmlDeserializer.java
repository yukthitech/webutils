package com.yukthitech.webutils.common.annotations.xml;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.yukthitech.ccg.xml.XMLBeanParser;

/**
 * Used to mark a field to be converted to json-with-type string during deserialization.
 * This is helpful when abstract types are used whose actual type is decided at runtime.
 * 
 * @author akiran
 */
public class XmlDeserializer extends StdDeserializer<Object>
{
	private static final long serialVersionUID = 1L;

	/**
	 * Instantiates a new json with type deserializer.
	 */
	public XmlDeserializer()
	{
		super((Class<?>) null);
	}
	
	@Override
	public Object deserialize(JsonParser parser, DeserializationContext ctxt) throws IOException, JsonProcessingException
	{
		String text = parser.getText();
		
		ByteArrayInputStream bis = new ByteArrayInputStream(text.getBytes());
		return XMLBeanParser.parse(bis);
	}
}
