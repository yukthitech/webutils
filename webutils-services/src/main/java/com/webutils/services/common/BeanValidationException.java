package com.webutils.services.common;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

/**
 * Thrown when bean validation fails outside of controller binding.
 */
public class BeanValidationException extends RuntimeException
{
	private static final long serialVersionUID = 1L;

	private final List<PropertyError> errors = new ArrayList<>();

	public void addError(String field, String message)
	{
		errors.add(new PropertyError(field, message));
	}

	public List<PropertyError> getErrors()
	{
		return errors;
	}

	@Data
	public static class PropertyError
	{
		private final String field;
		private final String message;
	}
}
