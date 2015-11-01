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

package com.yukthi.webutils.common;

/**
 * Constants to be used for actions
 * @author akiran
 */
public interface IActionConstants
{
	/**
	 * Action to be used for fetch
	 */
	public String ACTION_TYPE_FETCH = "fetch";
	
	
	////////////////////////////////////////////////////////////////////////////
	/**
	 * Action prefix that can be used on LOV controller
	 */
	public String ACTION_PREFIX_LOV = "lov";
	
	/**
	 * Actions prefix used on model def controller
	 */
	public String ACTION_PREFIX_MODEL_DEF = "modelDef";

	////////////////////////////////////////////////////////////////////////////
	/**
	 * Parameter with name "name"
	 */
	public String PARAM_NAME = "name";
	
	/**
	 * Parameter with name "type"
	 */
	public String PARAM_TYPE = "type";
	
	/////////////////////////////////////////////////////////////////////////////
	/**
	 * LOV fetch action name
	 */
	public String ACTION_LOV_FETCH = ACTION_PREFIX_LOV + "." + ACTION_TYPE_FETCH;
	
	public String ACTION_MODEL_DEF_FETCH = ACTION_PREFIX_MODEL_DEF + "." + ACTION_TYPE_FETCH;
}
