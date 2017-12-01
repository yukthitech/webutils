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

package com.yukthitech.webutils.repository;

import java.util.List;
import java.util.Set;

import com.yukthitech.persistence.repository.annotations.Condition;
import com.yukthitech.persistence.repository.annotations.Field;
import com.yukthitech.persistence.repository.annotations.OrderBy;
import com.yukthitech.persistence.repository.search.SearchQuery;
import com.yukthitech.webutils.annotations.RestrictBySpace;
import com.yukthitech.webutils.annotations.SearchQueryMethod;
import com.yukthitech.webutils.common.extensions.ExtensionFieldSearchQuery;
import com.yukthitech.webutils.common.extensions.ExtensionFieldSearchResult;

/**
 * Repository for entity extension fields.
 * @author akiran
 */
public interface IExtensionFieldRepository extends IWebutilsRepository<ExtensionFieldEntity>
{
	/**
	 * Finder method to find extension fields for specified entity extension.
	 * @param extensionName Extension name for which fields needs to be fetched
	 * @return List of matching fields
	 */
	@RestrictBySpace
	public List<ExtensionFieldEntity> findExtensionFields(@Condition("extension.name") String extensionName);
	
	/**
	 * Fetches extension fields of specified entity under all owners restricted to current user space.
	 * This is mainly used for search query customization.
	 * 
	 * @param entityType Entity type for which extension fields needs to be fetched.
	 * @return Matching extension fields.
	 */
	@RestrictBySpace
	public List<ExtensionFieldEntity> findExtensionFieldsByEntity(@Condition("extension.targetEntityType") String entityType);

	/**
	 * Fetches extension fields by extension id.
	 * @param extensionId Extension id
	 * @return Matching extension fields
	 */
	@RestrictBySpace
	public List<ExtensionFieldEntity> findExtensionFieldsByExtensionId(@Condition("extension.id") long extensionId);

	/**
	 * Fetches the extension field for specified extension with specified id.
	 * @param extensionName Extension under which field is defined
	 * @param id Id of the field that needs to be fetched
	 * @return Extension field
	 */
	@RestrictBySpace
	public ExtensionFieldEntity findExtensionField(@Condition("extension.name") String extensionName, @Condition("id") long id);

	/**
	 * Fetches extension id for specified field id.
	 * @param id Field id for which extension needs to be fetched
	 * @return Extension id
	 */
	@RestrictBySpace
	@Field("extension.id")
	public long fetchExtensionIdById(long id);
	
	/**
	 * Deletes all extension fields of all owners.
	 */
	public void deleteAll();
	
	/**
	 * Search query to fetch extension fields.
	 * @param searchQuery query.
	 * @return Matching results.
	 */
	@SearchQueryMethod(name = "extensionFieldSearch", queryModel = ExtensionFieldSearchQuery.class)
	@OrderBy("name")
	public List<ExtensionFieldSearchResult> searchExtensionFields(SearchQuery searchQuery);

	/**
	 * Fetches used column names of the specified extension.
	 * @param extensionId Extension id for which column names needs to be fetched.
	 * @return Current used column names of specified extension.
	 */
	@RestrictBySpace
	@Field("columnName")
	public Set<String> fetchUsedColumnNames(@Condition("extension.id") long extensionId);
}
