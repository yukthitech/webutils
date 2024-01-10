package com.yukthitech.webutils.common.annotations.json;

import java.io.IOException;

import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectMapper.DefaultTyping;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.cfg.MapperConfig;
import com.fasterxml.jackson.databind.introspect.Annotated;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;
import com.fasterxml.jackson.databind.jsontype.TypeResolverBuilder;

/**
 * Used to mark a field to be converted to json-with-type string during
 * serialization and deserialization. This is helpful when abstract types are
 * used whose actual type is decided at runtime.
 * 
 * @author akiran
 */
public class JsonWithTypeSerializer extends JsonSerializer<Object>
{
	/**
	 * Object mapper with types and also which skips nested conversion.
	 */
	public static final ObjectMapper OBJECT_MAPPER_WITH_TYPE = new ObjectMapper()
	{
		private static final long serialVersionUID = 1L;
		{
			enableDefaultTyping(DefaultTyping.NON_FINAL, As.PROPERTY);

			JacksonAnnotationIntrospector ignoreJsonTypeInfoIntrospector = new JacksonAnnotationIntrospector()
			{
				private static final long serialVersionUID = 1L;

				@Override
				protected TypeResolverBuilder<?> _findTypeResolver(MapperConfig<?> config, Annotated ann, JavaType baseType)
				{
					if(ann.hasAnnotation(DataWithDynamicTypes.class) || ann.hasAnnotation(JsonSerialize.class) || ann.hasAnnotation(JsonDeserialize.class))
					{
						return null;
					}
					
					return super._findTypeResolver(config, ann, baseType);
				}
			};
			
			this.setAnnotationIntrospector(ignoreJsonTypeInfoIntrospector);
		}
	};

	@SuppressWarnings("rawtypes")
	@Override
	public void serialize(Object value, JsonGenerator gen, SerializerProvider serializers) throws IOException, JsonProcessingException
	{
		String str = null;

		if(value instanceof Enum)
		{
			Enum<?> en = (Enum<?>) value;
			str = String.format("[\"%s\", \"%s\"]", ((Enum) en).getDeclaringClass().getName(), en.name());
		}
		else
		{
			str = OBJECT_MAPPER_WITH_TYPE.writeValueAsString(value);
		}

		gen.writeObject(str);
	}
}
