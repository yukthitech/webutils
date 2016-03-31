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

package com.yukthi.webutils.repository.file;

import java.util.Date;
import java.util.List;

import com.yukthi.persistence.ICrudRepository;
import com.yukthi.persistence.repository.annotations.Condition;
import com.yukthi.persistence.repository.annotations.DefaultCondition;
import com.yukthi.persistence.repository.annotations.Field;
import com.yukthi.persistence.repository.annotations.MethodConditions;
import com.yukthi.persistence.repository.annotations.Operator;
import com.yukthi.persistence.repository.annotations.SearchResult;
import com.yukthi.webutils.common.FileInfo;

/**
 * Repository for managing files in db.
 * @author akiran
 */
public interface IFileRepository extends ICrudRepository<FileEntity>
{
	/**
	 * Fetches the file entity based on id and security flag.
	 * @param id Id of the file to update
	 * @param secured Expected security file flag
	 * @return Matching file entity
	 */
	public FileEntity findBySecurityFlag(@Condition("id") long id, @Condition("secured") boolean secured);
	
	/**
	 * Fetches file info for specified id.
	 * @param id Id for which file info needs to be fetched
	 * @return Matching file information
	 */
	@SearchResult
	public FileInfo fetchFileInfo(@Condition("id") long id);

	/**
	 * Fetches file information list based on specified owner details.
	 * @param ownerEntityType Owner entity type
	 * @param ownerEntityField Owner entity field
	 * @param ownerEntityId Owner entity id
	 * @return List of matching file information
	 */
	@SearchResult
	public List<FileInfo> fetchByOwner(@Condition("ownerEntityType") String ownerEntityType, 
			@Condition("ownerEntityField") String ownerEntityField, @Condition("ownerEntityId") Long ownerEntityId);

	/**
	 * Fetches file entity based on specified owner details.
	 * @param ownerEntityType Owner entity type
	 * @param ownerEntityField Owner entity field
	 * @param ownerEntityId Owner entity id
	 * @return Matching file entity
	 */
	public FileEntity fetchEntityByOwner(@Condition("ownerEntityType") String ownerEntityType, 
			@Condition("ownerEntityField") String ownerEntityField, @Condition("ownerEntityId") Long ownerEntityId);

	/**
	 * Fetches file ids of specified owner.
	 * @param ownerEntityType Owner entity type
	 * @param ownerEntityField Owner field
	 * @param ownerEntityId Owner entity id
	 * @return List of matching file ids
	 */
	@Field("id")
	public List<Long> fetchIdsByOwner(@Condition("ownerEntityType") String ownerEntityType, 
			@Condition("ownerEntityField") String ownerEntityField, @Condition("ownerEntityId") Long ownerEntityId);

	/**
	 * Fetches file informations based on custom attribute.
	 * @param customAttribute1 Custom attribute 1
	 * @param customAttribute2 Custom attribute 2
	 * @param customAttribute3 Custom attribute 3
	 * @return List of matching file informations
	 */
	@SearchResult
	public List<FileInfo> fetchWithCustomAttributes(@Condition("customAttribute1") String customAttribute1, 
			@Condition("customAttribute2") String customAttribute2,
			@Condition("customAttribute3") String customAttribute3);

	/**
	 * Deletes the files for specified owner details.
	 * @param ownerEntityType Owner entity type
	 * @param ownerEntityField Owner entity field
	 * @param ownerEntityId Owner entity id
	 * @return Number of files deleted
	 */
	public int deleteByOwner(@Condition("ownerEntityType") String ownerEntityType, 
			@Condition("ownerEntityField") String ownerEntityField, @Condition("ownerEntityId") Long ownerEntityId);
	
	/**
	 * Updates specified temporary file to permanent file. If non temporary file specified, this
	 * method simply returns false.
	 * @param id Id of the file
	 * @param ownerEntityType Owner entity type
	 * @param ownerEntityField Owner entity field
	 * @param ownerEntityId Owner entity id
	 * @return True if conversion was successful
	 */
	@MethodConditions(conditions = @DefaultCondition(field = "ownerEntityId", value = "0"))
	public boolean updateToPermanentFile(@Condition("id") long id, @Field("ownerEntityType") String ownerEntityType, 
			@Field("ownerEntityField") String ownerEntityField, @Field("ownerEntityId") Long ownerEntityId);
	
	/**
	 * Deletes all temp files which are created after specified date and time.
	 * @param createdAfter Date/time after which temp files created should be deleted.
	 */
	@MethodConditions(conditions = @DefaultCondition(field = "ownerEntityId", value = "0"))
	public void deleteTempFiles(@Condition(value = "createdOn", op = Operator.GE) Date createdAfter);
}
