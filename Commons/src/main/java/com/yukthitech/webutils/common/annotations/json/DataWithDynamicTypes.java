package com.yukthitech.webutils.common.annotations.json;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.fasterxml.jackson.annotation.JacksonAnnotationsInside;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * Used to mark target field as a field with dynamic data types, so custom serialization and deserialization
 * will happen.
 * @author akiran
 */
@JacksonAnnotationsInside
@JsonSerialize(using = JsonWithTypeSerializer.class)
@JsonDeserialize(using = JsonWithTypeDeserializer.class)
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface DataWithDynamicTypes
{
}
