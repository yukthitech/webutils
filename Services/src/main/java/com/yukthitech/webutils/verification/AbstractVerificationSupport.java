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
package com.yukthitech.webutils.verification;

import java.util.Set;

import com.yukthitech.utils.CommonUtils;

/**
 * Abstraction class for verification support.
 */
public abstract class AbstractVerificationSupport implements IVerificationSupport
{
	/**
	 * Type of verification supported.
	 */
	private Set<String> types;
	
	public AbstractVerificationSupport(String... types)
	{
		this.types = CommonUtils.toSet(types);
	}
	
	@Override
	public Set<String> getVerificationTypes()
	{
		return types;
	}
}
