package com.yukthitech.webutils.autox;

import java.util.List;

import com.yukthitech.persistence.repository.annotations.OrderBy;
import com.yukthitech.persistence.repository.search.SearchQuery;
import com.yukthitech.webutils.annotations.SearchQueryMethod;
import com.yukthitech.webutils.repository.IWebutilsRepository;

/**
 * Repository for autox report.
 * @author akiran
 */
public interface IAutoxReportRepository extends IWebutilsRepository<AutoxReportEntity>
{
	/**
	 * Search query for autox reports.
	 * @param searchQuery query to execute
	 * @return matching results
	 */
	@SearchQueryMethod(name = "autoxReportSearch", queryModel = AutoxReportSearchQuery.class)
	@OrderBy("createdOn")
	public List<AutoxReportSearchResult> searchCandidates(SearchQuery searchQuery);
}
