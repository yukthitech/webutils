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

package com.yukthi.webutils.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.yukthi.persistence.repository.annotations.SearchFunction;
import com.yukthi.persistence.repository.annotations.SearchResult;
import com.yukthi.webutils.repository.search.ISearchResultCustomizer;
import com.yukthi.webutils.services.SearchService;

/**
 * Used to mark a repository method as search query method. So that when required
 * the method can be invoked for search data fetching.
 * @author akiran
 */
@RegistryMethod(registryType = SearchService.class, dynamic = false)
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@SearchResult
@SearchFunction
public @interface SearchQueryMethod
{
	/**
	 * Name of the Search-query to be used by client.
	 * @return Name of the Search-query to be used by client
	 */
	public String name();
	
	/**
	 * Model bean to be used for query.
	 * @return Search query model type
	 */
	public Class<?> queryModel();
	
	/**
	 * Customizer is used for searchResult to customized.
	 *
	 * @return customized searchResults
	 */
	@SuppressWarnings("rawtypes")
	public Class<? extends ISearchResultCustomizer> customizer() default ISearchResultCustomizer.class;
}

