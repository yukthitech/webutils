/*
 * The MIT License (MIT)
 * Copyright (c) 2015 "Yukthi Techsoft Pvt. Ltd." (http://yukthi-tech.co.in)

 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.yukthi.webutils.services;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Path;
import javax.validation.Validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.MethodArgumentNotValidException;

import com.yukthi.webutils.BeanValidationException;

/**
 * Service to validate beans based on validation framework
 * @author akiran
 */
@Service
public class ValidationService
{
	@Autowired
	private Validator validator;

	/**
	 * Fetches property name of the specified violation path
	 * @param violation
	 * @return
	 */
	private String getField(ConstraintViolation<?> violation)
	{
		StringBuilder builder = new StringBuilder();

		for(Path.Node node : violation.getPropertyPath())
		{
			builder.append(node.getName());
			builder.append(".");
		}

		builder.deleteCharAt(builder.length() - 1);

		return builder.toString();
	}

	/**
	 * Validates specified object using validation framework. In case of violations
	 * exception will be thrown
	 * @param object
	 * @throws MethodArgumentNotValidException Exception to be thrown in case of violations
	 */
	public void validate(Object object) throws MethodArgumentNotValidException
	{
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
}
