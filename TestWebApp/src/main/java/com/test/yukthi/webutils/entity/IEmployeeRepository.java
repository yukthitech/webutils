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

package com.test.yukthi.webutils.entity;

import java.util.List;

import com.test.yukthi.webutils.Authorization;
import com.test.yukthi.webutils.SecurityRole;
import com.test.yukthi.webutils.models.TestEmpSearchQuery;
import com.test.yukthi.webutils.models.TestEmpSearchResult;
import com.yukthi.persistence.ICrudRepository;
import com.yukthi.persistence.repository.annotations.OrderBy;
import com.yukthi.persistence.repository.search.SearchQuery;
import com.yukthi.webutils.annotations.LovQuery;
import com.yukthi.webutils.annotations.SearchQueryMethod;
import com.yukthi.webutils.common.models.ValueLabel;
import com.yukthi.webutils.repository.IWebutilsRepository;

/**
 * @author akiran
 *
 */
public interface IEmployeeRepository extends IWebutilsRepository<EmployeeEntity>
{
	@LovQuery(name = "employeeLov", valueField = "id", labelField = "name")
	public List<ValueLabel> fetchEmployeeLov();
	
	@Authorization(SecurityRole.CLIENT_ADMIN)
	@LovQuery(name = "employeeLovAuthorized", valueField = "id", labelField = "name")
	public List<ValueLabel> fetchEmployeeLov1();

	@Authorization(SecurityRole.PROJ_ADMIN)
	@LovQuery(name = "employeeLovUnauthorized", valueField = "id", labelField = "name")
	public List<ValueLabel> fetchEmployeeLov2();
	
	
	
	@SearchQueryMethod(name = "empSearch", queryModel = TestEmpSearchQuery.class)
	@OrderBy("name")
	public List<TestEmpSearchResult> findEmployees(SearchQuery searchQuery);

	@Authorization(SecurityRole.PROJ_ADMIN)
	@SearchQueryMethod(name = "empSearchUnauthorized", queryModel = TestEmpSearchQuery.class)
	@OrderBy("name")
	public List<TestEmpSearchResult> findEmployees1(SearchQuery searchQuery);

	@Authorization(SecurityRole.CLIENT_ADMIN)
	@SearchQueryMethod(name = "empSearchAuthorized", queryModel = TestEmpSearchQuery.class)
	@OrderBy("name")
	public List<TestEmpSearchResult> findEmployees2(SearchQuery searchQuery);

	
	
	public void deleteAll();
}
