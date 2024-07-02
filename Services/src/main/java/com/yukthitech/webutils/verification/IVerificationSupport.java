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

/**
 * Abstraction of verification support.
 */
public interface IVerificationSupport
{
	/**
	 * Types of verification supported by this supporter.
	 * @return
	 */
	public Set<String> getVerificationTypes();
	
	/**
	 * Should send specified code to the specified type with specified value.
	 * @param type Type of verification to be done. Eg: phone, email, etc.
	 * @param value Value to be verified. Eg: phone number, email id, etc.
	 * @param code Code to be sent.
	 */
	public void sendCode(String type, String value, String code) throws CodeDeliveryException;
}
