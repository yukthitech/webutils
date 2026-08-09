package com.webutils.testapp.lov;

import java.util.Collection;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.webutils.services.common.WebutilsServiceSupport;

/**
 * Persists MultiEditableLovDemoModel into TEMP_TABLE after framework LOV processing
 * (case-insensitive map to existing options and/or create new STORED_LOV_OPTION rows).
 */
@Service
public class MultiEditableLovDemoService
{
	private static final Logger logger = LogManager.getLogger(MultiEditableLovDemoService.class);

	@Autowired
	private WebutilsServiceSupport webutilsServiceSupport;

	@Autowired
	private ITempTableRepository tempTableRepository;

	/**
	 * Processes LOV values on the model, then inserts a TEMP_TABLE row with categories JSON.
	 *
	 * @param model submitted demo model
	 * @return persisted entity with generated id and remapped categories JSON
	 */
	public TempTableEntity submit(MultiEditableLovDemoModel model)
	{
		webutilsServiceSupport.processModel(model, null);

		TempTableEntity entity = new TempTableEntity()
			.setCategories(toJsonArray(model.getCategories()));

		tempTableRepository.save(entity);

		logger.info("Multi editable LOV demo persisted TEMP_TABLE id={}, categories={}",
				entity.getId(), entity.getCategories());
		return entity;
	}

	private static String toJsonArray(Collection<String> values)
	{
		if(values == null || values.isEmpty())
		{
			return "[]";
		}

		return values.stream()
				.map(v -> "\"" + escapeJson(v) + "\"")
				.collect(Collectors.joining(",", "[", "]"));
	}

	private static String escapeJson(String value)
	{
		if(value == null)
		{
			return "";
		}

		return value
				.replace("\\", "\\\\")
				.replace("\"", "\\\"");
	}
}
