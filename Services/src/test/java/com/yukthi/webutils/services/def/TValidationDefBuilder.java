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

package com.yukthi.webutils.services.def;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.MessageSource;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.yukthi.utils.ReflectionUtils;
import com.yukthi.validation.annotations.GreaterThanEquals;

/**
 * @author akiran
 *
 */
public class TValidationDefBuilder
{
	private static Logger logger = LogManager.getLogger(TValidationDefBuilder.class);
	
	/**
	 * Checks message building when message needs to be fetched from reosurce bundle
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	public void testMessageBuilding_ExprMessage()
	{
		MessageSource mockMessageSource = mock(MessageSource.class);
		when(mockMessageSource.getMessage("com.test.message", null, null)).thenReturn("Value should be greater or equal to {field}'s value - {field}");
		
		GreaterThanEquals greaterThanEquals = mock(GreaterThanEquals.class);
		when(greaterThanEquals.annotationType()).thenReturn((Class)GreaterThanEquals.class);
		when(greaterThanEquals.field()).thenReturn("field123");
		when(greaterThanEquals.message()).thenReturn("{com.test.message}");
		
		ValidationDefBuilder builder = new ValidationDefBuilder();
		ReflectionUtils.setFieldValue(builder, "validationMessageSource", mockMessageSource);
	
		String finalMsg = builder.getMessage(greaterThanEquals);
		logger.debug("Got message as - " + finalMsg);
		
		Assert.assertEquals(finalMsg, "Value should be greater or equal to field123's value - field123");
	}

	/**
	 * Checks message building when message is specified with annotation
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	public void testMessageBuilding_annotMessage()
	{
		GreaterThanEquals greaterThanEquals = mock(GreaterThanEquals.class);
		when(greaterThanEquals.annotationType()).thenReturn((Class)GreaterThanEquals.class);
		when(greaterThanEquals.field()).thenReturn("field123");
		when(greaterThanEquals.message()).thenReturn("Value should be greater or equal to {field}'s value - ${value}");
		
		ValidationDefBuilder builder = new ValidationDefBuilder();

		String finalMsg = builder.getMessage(greaterThanEquals);
		logger.debug("Got message as - " + finalMsg);
		
		Assert.assertEquals(finalMsg, "Value should be greater or equal to field123's value - ${value}");
	}
}
