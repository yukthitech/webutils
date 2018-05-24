package com.yukthitech.webutils.common.annotations.xml;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.yukthitech.ccg.xml.writer.XmlBeanWriter;

/**
 * Used to mark a field to be converted to json-with-type string during
 * serialization and deserialization. This is helpful when abstract types are
 * used whose actual type is decided at runtime.
 * 
 * @author akiran
 */
public class XmlSerializer extends JsonSerializer<Object>
{
	@Override
	public void serialize(Object value, JsonGenerator gen, SerializerProvider serializers) throws IOException, JsonProcessingException
	{
		String str = XmlBeanWriter.writeToString("dynamic-data", value);
		gen.writeObject(str);
	}
}
