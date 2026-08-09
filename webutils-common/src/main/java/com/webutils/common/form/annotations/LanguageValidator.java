package com.webutils.common.form.annotations;

import java.io.StringReader;
import java.util.Set;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilderFactory;

import org.xml.sax.InputSource;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SchemaLocation;
import com.networknt.schema.SpecVersion;
import com.networknt.schema.ValidationMessage;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * Validates string content according to {@link Language#value()}.
 */
public class LanguageValidator implements ConstraintValidator<Language, String>
{
	private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

	private static final JsonSchemaFactory SCHEMA_FACTORY =
			JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V202012);

	private static final JsonSchema META_SCHEMA =
			SCHEMA_FACTORY.getSchema(SchemaLocation.of(SpecVersion.VersionFlag.V202012.getId()));

	private LanguageType languageType;

	@Override
	public void initialize(Language annotation)
	{
		this.languageType = annotation.value();
	}

	@Override
	public boolean isValid(String value, ConstraintValidatorContext context)
	{
		if(value == null || value.trim().isEmpty())
		{
			return true;
		}

		try
		{
			switch(languageType)
			{
				case JSON:
					validateJson(value);
					return true;
				case XML:
					validateXml(value);
					return true;
				case JSON_SCHEMA:
					validateJsonSchema(value);
					return true;
				case PYTHON:
					// No syntax validation yet — accept any non-blank content
					return true;
				default:
					return false;
			}
		}catch(Exception ex)
		{
			context.disableDefaultConstraintViolation();
			context.buildConstraintViolationWithTemplate(buildMessage(ex))
					.addConstraintViolation();
			return false;
		}
	}

	private void validateJson(String value) throws Exception
	{
		OBJECT_MAPPER.readTree(value);
	}

	private void validateXml(String value) throws Exception
	{
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
		factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
		factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
		factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
		factory.setExpandEntityReferences(false);
		factory.newDocumentBuilder().parse(new InputSource(new StringReader(value)));
	}

	private void validateJsonSchema(String value) throws Exception
	{
		JsonNode node = OBJECT_MAPPER.readTree(value);
		Set<ValidationMessage> errors = META_SCHEMA.validate(node);
		if(errors != null && !errors.isEmpty())
		{
			throw new IllegalArgumentException(errors.iterator().next().getMessage());
		}
	}

	private String buildMessage(Exception ex)
	{
		String detail = ex.getMessage();
		if(detail == null || detail.trim().isEmpty())
		{
			detail = ex.getClass().getSimpleName();
		}

		return "Invalid " + languageType.name().toLowerCase().replace('_', ' ') + " content: " + detail;
	}
}
