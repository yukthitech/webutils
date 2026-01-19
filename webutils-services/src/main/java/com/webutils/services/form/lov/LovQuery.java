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

package com.webutils.services.form.lov;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.yukthitech.persistence.repository.annotations.OrderBy;
import com.yukthitech.persistence.repository.annotations.ResultMapping;
import com.yukthitech.persistence.repository.annotations.SearchResult;
import com.yukthitech.utils.annotations.OverrideProperties;
import com.yukthitech.utils.annotations.OverrideProperty;

/**
 * Used to mark a repository method as LOV query method. So that when required
 * the method can be invoked for LOV data fetching.
 * @author akiran
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@SearchResult(mappings = {
		@ResultMapping(entityField = "valueField", property = "value"),
		@ResultMapping(entityField = "labelField", property = "label")
})
@OrderBy({"labelField"})
public @interface LovQuery
{
	/**
	 * Name of the LOV to be used by client.
	 * @return Name of the LOV to be used by client
	 */
	public String name();
	
	/**
	 * Specified field that needs to be used for "value" property of ValueLabel beans.
	 * @return Value field
	 */
	@OverrideProperty(targetAnnotationType = SearchResult.class, property = "mappings[0].entityField")
	public String valueField();
	
	/**
	 * Specified field that needs to be used for "label" property of ValueLabel beans.
	 * @return Label field
	 */
	@OverrideProperties({
		@OverrideProperty(targetAnnotationType = SearchResult.class, property = "mappings[1].entityField"),
		@OverrideProperty(targetAnnotationType = OrderBy.class, property = "value[0]")
	})
	public String labelField();
}
