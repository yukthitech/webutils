package com.yukthitech.webutils.alerts.event;

import java.util.List;

import com.yukthitech.persistence.repository.annotations.Condition;
import com.yukthitech.webutils.repository.IWebutilsRepository;

/**
 * Repository for message parsing rules.
 * @author akiran
 */
public interface IEventAlertRuleRepository extends IWebutilsRepository<EventAlertRuleEntity>
{
	
	/**
	 * Fetches alerts for specified event type.
	 *
	 * @param eventType the event type
	 * @return the list of matching alerts
	 */
	public List<EventAlertRuleEntity> fetchAlerts(@Condition("eventType") String eventType);
}
