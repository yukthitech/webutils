package com.webutils.testapp.sample;

import java.util.List;

import com.webutils.services.search.SearchQueryMethod;
import com.yukthitech.persistence.ICrudRepository;
import com.yukthitech.persistence.repository.annotations.OrderBy;
import com.yukthitech.persistence.repository.search.SearchQuery;

/**
 * Repository for SAMPLE_ITEM and {@code sampleItemSearch}.
 */
public interface ISampleItemRepository extends ICrudRepository<SampleItemEntity>
{
	@SearchQueryMethod(name = "sampleItemSearch", queryModel = SampleItemSearchQuery.class)
	@OrderBy("name")
	List<SampleItemSearchResult> searchSampleItems(SearchQuery searchQuery);
}
