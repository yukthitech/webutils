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

package com.webutils.common.form.model;

import java.util.Map;

import lombok.Data;

/**
 * Indicates validation on a model field. That can be used on client side to
 * generate dynamic validations.
 * 
 * @author akiran
 */
@Data
public class ValidationDef
{
	/**
	 * Name of the validator. Validators on the client should match with this
	 * name.
	 */
	private String name;

	/**
	 * Internalized error message to be used in case validation fails. Will have
	 * expressions using ${} pattern. ${value} should hold the user input value
	 */
	private String errorMessage;
	
	/**
	 * Indicates this is cross validation (validates against other field)
	 */
	private boolean crossValidation;

	/**
	 * Value/config map that can be used to specify parameters for validator (obtained from annotations).
	 */
	private Map<String, Object> config;
}
