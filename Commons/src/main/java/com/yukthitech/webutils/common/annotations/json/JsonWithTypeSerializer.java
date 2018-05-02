package com.yukthitech.webutils.common.annotations.json;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.yukthitech.webutils.common.IWebUtilsCommonConstants;

/**
 * Used to mark a field to be converted to json-with-type string during serialization and deserialization.
 * This is helpful when abstract types are used whose actual type is decided at runtime.
 * @author akiran
 */
public class JsonWithTypeSerializer extends JsonSerializer<Object>
{
	@Override
	public void serialize(Object value, JsonGenerator gen, SerializerProvider serializers) throws IOException, JsonProcessingException
	{
		String str = null;
		
		if(value instanceof Enum)
		{
			Enum<?> en = (Enum<?>) value;
			str = String.format("[\"%s\", \"%s\"]", en.getClass().getSigners(), en.name());
		}
		else
		{
			IWebUtilsCommonConstants.OBJECT_MAPPER_WITH_TYPE.writeValueAsString(value);
		}
		
		gen.writeObject(str);
	}
}
