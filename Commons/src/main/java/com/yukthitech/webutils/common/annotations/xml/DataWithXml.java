package com.yukthitech.webutils.common.annotations.xml;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.fasterxml.jackson.annotation.JacksonAnnotationsInside;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * Used to mark target field as a field with dynamic data types, so custom xml based serialization and deserialization
 * will happen.
 * @author akiran
 */
@JacksonAnnotationsInside
@JsonSerialize(using = XmlSerializer.class)
@JsonDeserialize(using = XmlDeserializer.class)
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface DataWithXml
{
}
