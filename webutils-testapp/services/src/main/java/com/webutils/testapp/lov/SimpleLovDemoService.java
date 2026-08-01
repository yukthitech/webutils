package com.webutils.testapp.lov;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.webutils.services.common.InvalidRequestException;
import com.webutils.services.common.WebutilsServiceSupport;
import com.webutils.services.form.lov.stored.IStoredLovOptionRepository;
import com.webutils.services.form.lov.stored.StoredLovOptionEntity;

/**
 * Persists SimpleLovDemoModel into TEMP_TABLE by resolving the selected option id to its label.
 */
@Service
public class SimpleLovDemoService
{
	private static final Logger logger = LogManager.getLogger(SimpleLovDemoService.class);

	@Autowired
	private WebutilsServiceSupport webutilsServiceSupport;

	@Autowired
	private IStoredLovOptionRepository storedLovOptionRepository;

	@Autowired
	private ITempTableRepository tempTableRepository;

	/**
	 * Validates the LOV id on the model, resolves the option label, then inserts a TEMP_TABLE row.
	 *
	 * @param model submitted demo model with category option id
	 * @return persisted entity with generated id and category label
	 */
	public TempTableEntity submit(SimpleLovDemoModel model)
	{
		webutilsServiceSupport.processModel(model, null);

		StoredLovOptionEntity option = storedLovOptionRepository.findById(model.getCategoryId());

		if(option == null)
		{
			throw new InvalidRequestException("Unknown CATEGORY option id: {}", model.getCategoryId());
		}

		TempTableEntity entity = new TempTableEntity()
			.setCategory(option.getLabel());

		tempTableRepository.save(entity);

		logger.info("Simple LOV demo persisted TEMP_TABLE id={}, category={} (optionId={})",
				entity.getId(), entity.getCategory(), model.getCategoryId());
		return entity;
	}
}
