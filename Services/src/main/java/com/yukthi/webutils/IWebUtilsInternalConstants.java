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

package com.yukthi.webutils;

/**
 * Constants used by webutils internally
 * @author akiran
 */
public interface IWebUtilsInternalConstants
{
	/**
	 * Base package of web-utils framework
	 */
	public String WEBUTILS_BASE_PACKAGE = "com.yukthi.webutils";
	
	public String EXTENSIONS_REPO_BASE_PACKAGE = "com.yukthi.webutils.repository.extensions";
	
	/**
	 * Request attribute to maintian current user details
	 */
	public String REQ_ATTR_USER_DETAILS = "request.userDetails";
	
	public String CONTEXT_ATTR_CURRENT_USER = "currentUser";
	
	/**
	 * Context attribute name, that will be used to hold current lov dependency field value.
	 */
	public String CONTEXT_ATTR_LOV_DEPENDENCY_VAL = "lovDependencyValue";
}
