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

import java.lang.reflect.AnnotatedElement;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import com.yukthi.webutils.common.annotations.Description;
import com.yukthi.webutils.common.annotations.Label;

/**
 * Util methods used across factories
 * @author akiran
 */
@Component
public class DefUtils
{
	@Autowired
	@Qualifier("labelsMessageSource")
	private MessageSource messageSource;
	
	@Autowired
	private HttpServletRequest request;
	
	/**
	 * Tries to fetch value from message source for give name
	 * @param name Message source key
	 * @return Value from message source, if present.
	 */
	private String getMessageSourceString(String name)
	{
		try
		{
			return messageSource.getMessage(name, null, request.getLocale()).trim();
		}catch(Exception ex)
		{
			return null;
		}
	}
	
	/**
	 * Computes default label by converting camel case names to space separated labels
	 * @param name Name to be converted into label
	 * @return computed label
	 */
	private String getDefaultLabel(String name)
	{
		char ch = name.charAt(0);
		String label = Character.toUpperCase(ch) + name.substring(1);
		label = label.replaceAll("([A-Z])", " $1");

		return label.trim();
	}
	
	/**
	 * Tries to fetch label based {@link Label} annotation. If not present, tries to fetch label from message source,
	 * if not found converts "name" into space delimited string and returns the same. 
	 * @param element Element for which label needs to be computed
	 * @param name Simple name of the element
	 * @param fqn Full qualified name of the element
	 * @return Computed label
	 */
	public String getLabel(AnnotatedElement element, String name, String fqn)
	{
		//check in message source with fqn
		String labelStr = getMessageSourceString(fqn + ".label");
		
		if(labelStr != null)
		{
			return labelStr;
		}
		
		//check in message source with simple name
		labelStr = getMessageSourceString(name + ".label");
		
		if(labelStr != null)
		{
			return labelStr;
		}
		
		//try to fetch based on Label annotation
		Label label = element.getAnnotation(Label.class);
		
		if(label != null)
		{
			return label.value();
		}
		
		//convert element name into label
		return getDefaultLabel(name);
	}

	/**
	 * Tries to fetch label based {@link Label} annotation. If not present, tries to fetch label from message source,
	 * if not found converts "name" into space delimited string and returns the same. 
	 * @param element Element for which label needs to be computed
	 * @param name Simple name of the element
	 * @param fqn Full qualified name of the element
	 * @return Computed label
	 */
	public String getDescription(AnnotatedElement element, String name, String fqn)
	{
		//check in message source with fqn
		String descStr = getMessageSourceString(fqn + ".description");
		
		if(descStr != null)
		{
			return descStr;
		}
		
		//check in message source with simple name
		descStr = getMessageSourceString(name + ".description");
		
		if(descStr != null)
		{
			return descStr;
		}
		
		//try to fetch based on Label annotation
		Description desc = element.getAnnotation(Description.class);
		
		if(desc != null)
		{
			return desc.value();
		}
		
		return null;
	}
}
