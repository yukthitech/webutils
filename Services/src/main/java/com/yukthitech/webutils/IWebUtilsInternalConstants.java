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

package com.yukthitech.webutils;

import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;

import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectMapper.DefaultTyping;

/**
 * Constants used by webutils internally.
 * @author akiran
 */
public interface IWebUtilsInternalConstants
{
	/**
	 * Base package of web-utils framework
	 */
	public String WEBUTILS_BASE_PACKAGE = "com.yukthitech.webutils";
	
	public String EXTENSIONS_REPO_BASE_PACKAGE = "com.yukthitech.webutils.repository.extensions";
	
	/**
	 * Request attribute to maintain current user details.
	 */
	public String REQ_ATTR_USER_DETAILS = "request.userDetails";
	
	/**
	 * Request attribute name to maintain current session token.
	 */
	public String REQ_ATTR_SESSION_TOKEN = "request.sessionToken";
	
	public String CONTEXT_ATTR_CURRENT_USER = "currentUser";
	
	/**
	 * Context attribute name, that will be used to hold current lov dependency field value.
	 */
	public String CONTEXT_ATTR_LOV_DEPENDENCY_VAL = "lovDependencyValue";
	
	/**
	 * Maximum length of the extension field.
	 */
	public int MAX_EXT_FIELD_LENGTH = 600;
	
	/**
	 * Prefix for the extension fields.
	 */
	public String EXT_FIELD_PREFIX = "FLD";
	
	/**
	 * Number of extension fields per entity.
	 */
	public int EXT_FIELD_COUNT = 100;
	
	/**
	 * Extension value holder entity field name.
	 */
	public String EXTENSIONS_FIELD_NAME = "extendedFields";

	/**
	 * Spring expression parser for parsing SPEL expressions.
	 */
	public ExpressionParser SPRING_EXPRESSION_PARSER = new SpelExpressionParser();
	
	/**
	 * Cache group to be used when dealing with more than one entity (or group of entities).
	 */
	public String CACHE_GROUP_GROUPED = "'groupedData'";
}
