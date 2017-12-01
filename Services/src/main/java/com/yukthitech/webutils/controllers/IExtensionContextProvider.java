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

package com.yukthitech.webutils.controllers;

import com.yukthitech.webutils.common.IExtendableModel;
import com.yukthitech.webutils.extensions.ExtensionDetails;
import com.yukthitech.webutils.extensions.ExtensionEntityDetails;

/**
 * Extension helper to be provided by the web applications.
 * @author akiran
 */
public interface IExtensionContextProvider
{
	/**
	 * Should provide owner and other custom extension details. This method should also take care of 
	 * required authorization.
	 * @param extensionName Extension name
	 * @param extensionPointDetails Extension point for which extension needs to be fetched
	 * @return Extension information with owner details
	 */
	public ExtensionDetails getExtensionDetails(String extensionName, ExtensionEntityDetails extensionPointDetails);
	
	/**
	 * Fetches extension name for specified model object. This method is generally invoked by framework
	 * for model when implements {@link IExtendableModel} but does not extension point defined on it. 
	 * @param modelObj Model object for which extension name needs to be fetched.
	 * @return Extension name for the model.
	 */
	public default String getExtensionName(Object modelObj)
	{
		return null;
	}
}
