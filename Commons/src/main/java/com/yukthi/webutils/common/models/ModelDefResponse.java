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

package com.yukthi.webutils.common.models;

import com.yukthi.webutils.common.models.def.ModelDef;

/**
 * Response representing Model definition
 * 
 * @author akiran
 */
public class ModelDefResponse extends BaseResponse
{
	/**
	 * Model definition
	 */
	private ModelDef modelDef;

	/**
	 * Instantiates a new lov list response.
	 */
	public ModelDefResponse()
	{}

	/**
	 * Instantiates a new model def response.
	 *
	 * @param modelDef the model def
	 */
	public ModelDefResponse(ModelDef modelDef)
	{
		this.modelDef = modelDef;
	}

	/**
	 * Gets the model definition.
	 *
	 * @return the model definition
	 */
	public ModelDef getModelDef()
	{
		return modelDef;
	}

	/**
	 * Sets the model definition.
	 *
	 * @param modelDef the new model definition
	 */
	public void setModelDef(ModelDef modelDef)
	{
		this.modelDef = modelDef;
	}

}
