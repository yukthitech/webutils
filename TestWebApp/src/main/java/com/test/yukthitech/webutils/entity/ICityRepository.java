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

package com.test.yukthitech.webutils.entity;

import static com.yukthitech.webutils.IWebUtilsInternalConstants.CONTEXT_ATTR_LOV_DEPENDENCY_VAL;

import java.util.List;

import com.yukthitech.persistence.repository.annotations.Condition;
import com.yukthitech.webutils.common.annotations.ContextAttribute;
import com.yukthitech.webutils.common.lov.ValueLabel;
import com.yukthitech.webutils.lov.LovQuery;
import com.yukthitech.webutils.repository.IWebutilsRepository;

/**
 * @author akiran
 *
 */
public interface ICityRepository extends IWebutilsRepository<CityEntity>
{
	@LovQuery(name = "cityLov", valueField = "id", labelField = "name")
	public List<ValueLabel> fetchCityLov(@ContextAttribute(CONTEXT_ATTR_LOV_DEPENDENCY_VAL) @Condition("state.id") long stateId);
	
	public void deleteAll();
}
