package com.webutils.services.search;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.webutils.common.auth.NoAuthentication;
import com.webutils.common.response.BaseResponse;
import com.webutils.common.response.BasicReadResponse;
import com.webutils.common.response.BasicSaveResponse;
import com.webutils.common.search.SearchSettingsModel;
import com.webutils.services.common.InvalidRequestException;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/search/settings")
public class SearchSettingsController
{
	@Autowired
	private SearchSettingsService service;

	@Autowired
	private SearchService searchService;

	@NoAuthentication
	@PostMapping("/save")
	public BasicSaveResponse save(@Valid @RequestBody SearchSettingsModel model)
	{
		validateSearchQueryName(model.getSearchQueryName());
		SearchSettingsEntity entity = service.save(model);
		return new BasicSaveResponse(entity.getId() != null ? entity.getId() : 0L);
	}

	@NoAuthentication
	@PostMapping("/update")
	public BaseResponse update(@Valid @RequestBody SearchSettingsModel model)
	{
		validateSearchQueryName(model.getSearchQueryName());
		service.update(model);
		return new BaseResponse();
	}

	@NoAuthentication
	@GetMapping("/read/{queryName}")
	public BasicReadResponse<SearchSettingsModel> fetch(@PathVariable("queryName") String searchQueryName)
	{
		return new BasicReadResponse<>(service.fetch(searchQueryName));
	}

	@NoAuthentication
	@DeleteMapping("/delete/{queryName}")
	public BaseResponse delete(@PathVariable("queryName") String searchQueryName)
	{
		service.deleteByName(searchQueryName);
		return new BaseResponse();
	}

	@NoAuthentication
	@DeleteMapping("/deleteAll")
	public BaseResponse deleteAll()
	{
		service.deleteAll();
		return new BaseResponse();
	}

	private void validateSearchQueryName(String searchQueryName)
	{
		if(!searchService.isValidSearchQuery(searchQueryName))
		{
			throw new InvalidRequestException("Invalid search query name specified - {}", searchQueryName);
		}
	}
}
