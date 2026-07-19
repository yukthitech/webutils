package com.webutils.testapp.lov;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.webutils.services.common.WebutilsServiceSupport;

/**
 * Persists LovDemoModel into TEMP_TABLE after framework LOV processing
 * (case-insensitive map to existing option or create new STORED_LOV_OPTION).
 */
@Service
public class LovDemoService
{
	private static final Logger logger = LogManager.getLogger(LovDemoService.class);

	@Autowired
	private WebutilsServiceSupport webutilsServiceSupport;

	@Autowired
	private ITempTableRepository tempTableRepository;

	/**
	 * Processes LOV values on the model, then inserts a TEMP_TABLE row.
	 *
	 * @param model submitted demo model
	 * @return persisted entity with generated id and remapped category
	 */
	public TempTableEntity submit(LovDemoModel model)
	{
		webutilsServiceSupport.processModel(model, null);

		TempTableEntity entity = new TempTableEntity()
			.setCategory(model.getCategory());

		tempTableRepository.save(entity);

		logger.info("LOV demo persisted TEMP_TABLE id={}, category={}", entity.getId(), entity.getCategory());
		return entity;
	}
}
