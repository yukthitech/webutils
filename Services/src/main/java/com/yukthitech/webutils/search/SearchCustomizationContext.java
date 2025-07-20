/**
 * Copyright (c) 2022 "Yukthi Techsoft Pvt. Ltd." (http://yukthitech.com)
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.yukthitech.webutils.search;

import java.lang.reflect.Method;

import com.yukthitech.persistence.ICrudRepository;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class SearchCustomizationContext
{
	/**
	 * Repository in which the search method is defined.
	 */
	private Class<? extends ICrudRepository<?>> repositoryType;
	
	/**
	 * Search method being invoked.
	 */
	private Method method;

	/**
	 * Name of search query.
	 */
	private String searchQueryName;
	
	/**
	 * Search query pojo.
	 */
	private Object query;
}
