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

import java.util.List;

import com.yukthi.persistence.ICrudRepository;
import com.yukthi.persistence.repository.annotations.Condition;
import com.yukthi.persistence.repository.annotations.SearchResult;
import com.yukthi.webutils.ExtensionValueDetails;

/**
 * Repository for entity extension field values
 * @author akiran
 */
public interface IExtensionFieldValueRepository extends ICrudRepository<ExtensionFieldValueEntity>
{
	/**
	 * Finder method to find extension values for specified entity extension
	 * @param extensionId Extension id for which fields needs to be fetched
	 * @param entityId Entity id for which values needs to be fetched
	 * @return List of matching field values
	 */
	public List<ExtensionFieldValueEntity> findExtensionValues(@Condition("extensionField.extension.id") long extensionId, @Condition("entityId") long entityId);
	
	/**
	 * Finder method to fetch extension fields 
	 * @param extensionId
	 * @param entityId
	 * @return extension field values
	 */
	@SearchResult
	public List<ExtensionValueDetails> findExtensionValueDetails(@Condition("extensionField.extension.id") long extensionId, @Condition("entityId") long entityId);
	
	/**
	 * Deletes all extension field values for specified entity
	 * @param entityId Entity id for which extended values needs to be deleted
	 * @return Number of deleted records
	 */
	public int deleteByEntityId(@Condition("entityId") long entityId);
}
