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

import com.yukthitech.utils.MessageFormatter;

/**
 * Exception to be thrown when input request parameters is found to be invalid
 * @author akiran
 */
public class InvalidRequestParameterException extends RuntimeException
{
	private static final long serialVersionUID = 1L;
	
	/**
	 * Instantiates a new invalid request parameter exception.
	 *
	 * @param message the message
	 */
	public InvalidRequestParameterException(String message)
	{
		super(message);
	}

	/**
	 * Instantiates a new invalid request parameter exception.
	 *
	 * @param message the message
	 * @param cause the cause
	 */
	public InvalidRequestParameterException(String message, Throwable cause)
	{
		super(message, cause);
	}
	
	/**
	 * Var args version of constructor
	 * @param cause
	 * @param message
	 * @param args
	 */
	public InvalidRequestParameterException(Throwable cause, String message, Object... args)
	{
		super(MessageFormatter.format(message, args), cause);
	}

	/**
	 * Var args version of constructor
	 * @param message
	 * @param args
	 */
	public InvalidRequestParameterException(String message, Object... args)
	{
		super(MessageFormatter.format(message, args));
	}

}
