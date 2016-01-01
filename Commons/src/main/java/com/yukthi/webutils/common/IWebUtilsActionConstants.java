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
public interface IWebUtilsActionConstants
{
	/**
	 * Action to be used for fetch
	 */
	public String ACTION_TYPE_FETCH = "fetch";
	
	/**
	 * Action be be used for save
	 */
	public String ACTION_TYPE_SAVE = "save";
	
	/**
	 * Action be be used for update
	 */
	public String ACTION_TYPE_UPDATE = "update";
	
	/**
	 * Action be be used for delete
	 */
	public String ACTION_TYPE_DELETE = "delete";

	/**
	 * Action be be used for delete
	 */
	public String ACTION_TYPE_EXECUTE = "execute";

	/**
	 * Action be be used for exporting the data
	 */
	public String ACTION_TYPE_EXPORT = "export";

	/**
	 * Action be be used for deleting all 
	 */
	public String ACTION_TYPE_DELETE_ALL = "deleteAll";

	/**
	 * Action to be used for fetch query def
	 */
	public String ACTION_TYPE_FETCH_QUERY_DEF = "fetch.queryDef";
	
	/**
	 * Action to be used to fetch query result def
	 */
	public String ACTION_TYPE_FETCH_RESULT_DEF = "fetch.resultDef";
	
	/**
	 * Action to fetch extension field
	 */
	public String ACTION_TYPE_FETCH_FIELD = "fetch.field";
	
	/**
	 * Action to fetch file as attachment
	 */
	public String ACTION_TYPE_FETCH_ATTACHMENT = "fetch.attachment";

	////////////////////////////////////////////////////////////////////////////
	/**
	 * Action prefix that can be used on LOV controller
	 */
	public String ACTION_PREFIX_LOV = "lov";
	
	/**
	 * Action prefix that is used for search relation methods
	 */
	public String ACTION_PREFIX_SEARCH = "search";

	/**
	 * Actions prefix used on model def controller
	 */
	public String ACTION_PREFIX_MODEL_DEF = "modelDef";
	
	/**
	 * Actions prefix used for extensions controller
	 */
	public String ACTION_PREFIX_EXTENSIONS = "extensions";
	
	/**
	 * Actions prefix used for files controller
	 */
	public String ACTION_PREFIX_FILES = "files";

	////////////////////////////////////////////////////////////////////////////
	/**
	 * Parameter with name "name"
	 */
	public String PARAM_NAME = "name";
	
	/**
	 * Parameter with name "type"
	 */
	public String PARAM_TYPE = "type";
	
	/**
	 * Parameter with name "id"
	 */
	public String PARAM_ID = "id";

	/////////////////////////////////////////////////////////////////////////////
	/**
	 * LOV fetch action name
	 */
	public String ACTION_LOV_FETCH = ACTION_PREFIX_LOV + "." + ACTION_TYPE_FETCH;
	
	/**
	 * Model DEF fetch action name
	 */
	public String ACTION_MODEL_DEF_FETCH = ACTION_PREFIX_MODEL_DEF + "." + ACTION_TYPE_FETCH;
	
	/**
	 * Extension fetch action
	 */
	public String ACTION_FETCH_EXTENSION = ACTION_PREFIX_EXTENSIONS + "." + ACTION_LOV_FETCH;
}
