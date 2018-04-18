package com.yukthitech.webutils.alerts.event;

import java.util.List;

import org.springframework.stereotype.Service;

import com.yukthitech.webutils.services.BaseCrudService;

/**
 * Services for pull alerts.
 * @author akiran
 */
@Service
public class EventAlertRuleService extends BaseCrudService<EventAlertRuleEntity, IEventAlertRuleRepository>
{
	/**
	 * Fetches alerts for specified event type.
	 *
	 * @param eventType the event type
	 * @return the list of matching alerts
	 */
	public List<EventAlertRuleEntity> fetchAlerts(String eventType)
	{
		return super.repository.fetchAlerts(eventType);
	}
}
