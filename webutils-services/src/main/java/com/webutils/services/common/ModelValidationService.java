package com.webutils.services.common;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Path;
import jakarta.validation.Validator;

/**
 * Validates model beans using the jakarta validation framework.
 */
@Service
public class ModelValidationService
{
	@Autowired
	private Validator validator;

	public void validate(Object object)
	{
		if(object == null)
		{
			return;
		}

		Set<ConstraintViolation<Object>> violations = validator.validate(object);
		if(violations == null || violations.isEmpty())
		{
			return;
		}

		BeanValidationException exception = new BeanValidationException();
		for(ConstraintViolation<Object> violation : violations)
		{
			exception.addError(getField(violation), violation.getMessage());
		}
		throw exception;
	}

	private String getField(ConstraintViolation<?> violation)
	{
		StringBuilder builder = new StringBuilder();
		for(Path.Node node : violation.getPropertyPath())
		{
			builder.append(node.getName()).append(".");
		}
		if(!builder.isEmpty())
		{
			builder.deleteCharAt(builder.length() - 1);
		}
		return builder.toString();
	}
}
