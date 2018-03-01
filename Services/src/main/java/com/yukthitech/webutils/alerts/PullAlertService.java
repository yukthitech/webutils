package com.yukthitech.webutils.alerts;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.beanutils.BeanUtils;
import org.springframework.stereotype.Service;

import com.yukthitech.utils.exceptions.InvalidStateException;
import com.yukthitech.webutils.common.alerts.AlertDetails;
import com.yukthitech.webutils.common.alerts.PullAlertStatus;
import com.yukthitech.webutils.services.BaseCrudService;

/**
 * Services for pull alerts.
 * @author akiran
 */
@Service
public class PullAlertService extends BaseCrudService<PullAlertEntity, IPullAlertRepository>
{
	/**
	 * Fetches alerts for specified source. 
	 * @param source source for which alerts to be fetched
	 * @return matching alerts.
	 */
	public List<AlertDetails> fetchAlerts(String source)
	{
		List<PullAlertEntity> alerts = super.repository.fetchAlerts(source);
		
		if(alerts == null || alerts.isEmpty())
		{
			return Collections.emptyList();
		}
		
		List<AlertDetails> alertDetailsLst = new ArrayList<>(alerts.size());
		
		for(PullAlertEntity entity : alerts)
		{
			AlertDetails alertDetails = new AlertDetails();
			
			try
			{
				BeanUtils.copyProperties(alertDetails, entity);
			}catch(Exception ex)
			{
				throw new InvalidStateException("An error occurred while copying alert properties.", ex);
			}
			
			alertDetailsLst.add(alertDetails);
		}
		
		return alertDetailsLst;
	}
	
	/**
	 * Updates the status of specified alert.
	 * @param id id of alert to update
	 * @param status status to update
	 */
	public void updateStatus(long id, PullAlertStatus status)
	{
		super.repository.updateStatus(id, status);
	}
}
