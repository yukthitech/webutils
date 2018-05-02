package com.yukthitech.webutils.alerts;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.beanutils.BeanUtils;
import org.springframework.stereotype.Service;

import com.yukthitech.utils.exceptions.InvalidStateException;
import com.yukthitech.webutils.common.alerts.AlertDetails;
import com.yukthitech.webutils.common.alerts.AlertProcessedDetails;
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
	 * @param target target for which alerts to be fetched
	 * @return matching alerts.
	 */
	public List<AlertDetails> fetchAlerts(String target)
	{
		List<PullAlertEntity> alerts = super.repository.fetchAlerts(target);
		
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
	 * @param alertProcessedDetails Alert processing details
	 * @param status status to update
	 */
	public void updateStatus(long id, PullAlertStatus status, AlertProcessedDetails alertProcessedDetails)
	{
		super.repository.updateStatus(id, status, alertProcessedDetails);
	}
}
