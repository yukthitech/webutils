package com.webutils.services.search;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.webutils.common.auth.NoAuthentication;
import com.webutils.common.form.model.ModelDef;
import com.webutils.common.response.BasicReadResponse;
import com.webutils.common.search.ExecuteSearchResponse;
import com.webutils.common.search.SearchExecutionModel;
import com.webutils.services.common.AttachmentDownloadHelper;
import com.webutils.services.common.InvalidRequestException;
import com.webutils.services.common.ModelValidationService;
import com.yukthitech.excel.exporter.ExcelExporter;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

import java.io.File;

@RestController
@RequestMapping("/api/search")
public class SearchController
{
	private static final Logger logger = LogManager.getLogger(SearchController.class);

	@Autowired
	private SearchService searchService;

	@Autowired
	private ModelValidationService modelValidationService;

	@Autowired
	private HttpServletResponse response;

	private final ObjectMapper objectMapper = new ObjectMapper();
	private final ExcelExporter excelExporter = new ExcelExporter();

	@NoAuthentication
	@GetMapping("/{name}/query/def")
	public BasicReadResponse<ModelDef> fetchSearchQueryDef(@PathVariable("name") String queryName)
	{
		logger.trace("fetchSearchQueryDef is called for query - {}", queryName);
		return new BasicReadResponse<>(searchService.getSearhQueryDefinition(queryName));
	}

	@NoAuthentication
	@GetMapping("/{name}/result/def")
	public BasicReadResponse<ModelDef> fetchSearchResultDef(@PathVariable("name") String queryName)
	{
		logger.trace("fetchSearchResultDef is called for query - {}", queryName);
		return new BasicReadResponse<>(searchService.getSearhResultDefinition(queryName));
	}

	@NoAuthentication
	@GetMapping("/execute/{name}")
	public ExecuteSearchResponse executeSearch(@PathVariable("name") String queryName, @Valid SearchExecutionModel searchExecutionModel) throws Exception
	{
		logger.trace("executeSearch is called for query - {}", queryName);
		Object query = parseQuery(queryName, searchExecutionModel);
		modelValidationService.validate(query);
		return searchService.executeSearch(queryName, query, searchExecutionModel);
	}

	@NoAuthentication
	@GetMapping("/export/{name}")
	public void exportSearch(@PathVariable("name") String queryName, @Valid SearchExecutionModel searchExecutionModel) throws Exception
	{
		logger.trace("exportSearch is called for query - {}", queryName);
		Object query = parseQuery(queryName, searchExecutionModel);
		modelValidationService.validate(query);

		ModelDef searchResultDef = searchService.getSearhResultDefinition(queryName);
		ExecuteSearchResponse results = searchService.executeSearch(queryName, query, new SearchExecutionModel(1, false, true));

		SearchExcelDataReport searchExcelDataReport = new SearchExcelDataReport("Results", results);
		File tempFile = File.createTempFile(queryName, ".xls");
		excelExporter.generateExcelSheet(tempFile.getPath(), searchExcelDataReport);

		AttachmentDownloadHelper.sendFile(response, searchResultDef.getLabel() + ".xls", tempFile,
				AttachmentDownloadHelper.MIME_MS_EXCEL_FILE, true);
	}

	private Object parseQuery(String queryName, SearchExecutionModel searchExecutionModel) throws Exception
	{
		Class<?> queryType = searchService.getSearchQueryType(queryName);
		if(searchExecutionModel.getQueryModelJson() == null)
		{
			return null;
		}

		try
		{
			return objectMapper.readValue(searchExecutionModel.getQueryModelJson(), queryType);
		}
		catch(Exception ex)
		{
			throw new InvalidRequestException("Failed to convert input json to {}. Input json - {}",
					queryType.getName(), searchExecutionModel.getQueryModelJson(), ex);
		}
	}
}
