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

import com.yukthi.persistence.repository.annotations.Condition;
import com.yukthi.persistence.repository.annotations.Field;
import com.yukthi.persistence.repository.annotations.OrderBy;
import com.yukthi.persistence.repository.search.SearchQuery;
import com.yukthi.webutils.annotations.SearchQueryMethod;
import com.yukthi.webutils.common.extensions.ExtensionFieldSearchQuery;
import com.yukthi.webutils.common.extensions.ExtensionFieldSearchResult;

/**
 * Repository for entity extension fields.
 * @author akiran
 */
public interface IExtensionFieldRepository extends IWebutilsRepository<ExtensionFieldEntity>
{
	/**
	 * Finder method to find extension fields for specified entity extension
	 * @param extensionId Extension id for which fields needs to be fetched
	 * @return List of matching fields
	 */
	public List<ExtensionFieldEntity> findExtensionFields(@Condition("extension.id") long extensionId);
	
	/**
	 * Fetches the extension field for specified extension with specified id
	 * @param extensionName Extension under which field is defined
	 * @param id Id of the field that needs to be fetched
	 * @return Extension field
	 */
	public ExtensionFieldEntity findExtensionField(@Condition("extension.targetPointName") String extensionName, @Condition("id") long id);

	/**
	 * Fetches extension id for specified field id
	 * @param id Field id for which extension needs to be fetched
	 * @return Extension id
	 */
	@Field("extension.id")
	public long fetchExtensionIdById(long id);
	
	/**
	 * Deletes all extension fields of all owners
	 */
	public void deleteAll();
	
	@SearchQueryMethod(name = "extensionFieldSearch", queryModel = ExtensionFieldSearchQuery.class)
	@OrderBy("name")
	public List<ExtensionFieldSearchResult> searchExtensionFields(SearchQuery searchQuery);

}
