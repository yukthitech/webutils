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

package com.webutils.services.form.lov.stored;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import com.yukthitech.persistence.ICrudRepository;
import com.yukthitech.persistence.repository.annotations.Condition;
import com.yukthitech.persistence.repository.annotations.Field;
import com.yukthitech.persistence.repository.annotations.Operator;
import com.yukthitech.persistence.repository.annotations.OrderBy;

/**
 * Repository for managing stored LOV values.
 * @author akiran
 */
public interface IStoredLovOptionRepository extends ICrudRepository<StoredLovOptionEntity>
{
	@Field("label")
	public Set<String> fetchLovOptionLabels(
		@Condition("lov.name") String lovName, 
		@Condition(value = "label", ignoreCase = true, op = Operator.IN) Collection<String> optionLabels);

	@OrderBy(value = "label")
	public List<StoredLovOptionEntity> fetchLovOptions(
			@Condition("lov.name") String lovName);
	
	@OrderBy(value = "label")
	public List<StoredLovOptionEntity> fetchChildLovOptions(
			@Condition("parentOptionId") Long parentLovOptionId,
			@Condition("lov.name") String lovName
		);

	@Field("id")
	public Long fetchLovOptionId(
		@Condition("lov.name") String lovName, 
		@Condition("label") String optionLabel);
	
}
