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

package com.yukthi.webutils.repository;

import com.yukthi.persistence.repository.annotations.AggregateFunction;
import com.yukthi.persistence.repository.annotations.Condition;
import com.yukthi.persistence.repository.annotations.Field;
import com.yukthi.webutils.annotations.RestrictBySpace;

/**
 * Repository for entity extensions.
 * @author akiran
 */
public interface IExtensionRepository extends IWebutilsRepository<ExtensionEntity>
{
	/**
	 * Fetches extension by name.
	 * @param name Name of the extension to fetch.
	 * @return Matching extension.
	 */
	@RestrictBySpace
	public ExtensionEntity findExtensionByName(@Condition("name") String name);
	
	/**
	 * Fetches extension based on specified target and owner details.
	 * @param targetEntityType Target entity type
	 * @param ownerEntityType Owner entity type
	 * @param ownerId Owner id.
	 * @return matching extension entity.
	 */
	public ExtensionEntity findExtension(@Condition("targetEntityType") String targetEntityType, 
			@Condition("ownerEntityType") String ownerEntityType, @Condition("ownerEntityId") long ownerId);
	
	/**
	 * Checks if the specified name is valid extension or not.
	 * @param name Name to be checked.
	 * @return True if its valid extension.
	 */
	@AggregateFunction
	@RestrictBySpace
	public boolean isValidExtension(@Condition("name") String name);
	
	/**
	 * Updates name of the specified extension.
	 * @param id Id of the extension to update.
	 * @param name New name
	 * @return Success/failure
	 */
	@RestrictBySpace
	public boolean updateExtensionName(@Condition("id") long id, @Field("name") String name);
}
