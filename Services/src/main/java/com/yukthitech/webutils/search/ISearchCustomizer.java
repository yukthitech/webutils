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

/**
 * Can we used by web applications to customize the query before execution.
 * This is mainly useful when data from session has to be injected into query or to inject
 * some computed values.
 * 
 * @author akranthikiran
 */
public interface ISearchCustomizer
{
	/**
	 * Expected to customize the query object present in query details as per the need of app.
	 * @param queryDetails
	 */
	public void customizeQuery(SearchCustomizationContext queryDetails);
}
