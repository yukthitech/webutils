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

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import com.yukthi.persistence.ICrudRepository;
import com.yukthi.webutils.services.dynamic.DynamicMethod;

/**
 * Implementing classes should provide registry functionality for dynamic repository method 
 * annotated with particular annotation
 * @author akiran
 */
public interface IRepositoryMethodRegistry<A extends Annotation>
{
	/**
	 * Invoked to register a dynamic method marked by specified annotation
	 * @param method Dynamic method being registered
	 * @param annotation Annotation to mark target method as dynamic method
	 */
	public void registerDynamicMethod(DynamicMethod method, A annotation);
	
	/**
	 * Invoked to register non-dynamic method marked by specified annotation
	 * @param method Repository method to register
	 * @param annotation Annotation used to mark method as registry method
	 * @param repository Repository in which method is defined
	 */
	public void registerRepositoryMethod(Method method, A annotation, ICrudRepository<?> repository);
}
