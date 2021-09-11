package com.yukthitech.webutils.alerts;

import java.util.List;

import com.yukthitech.persistence.repository.annotations.Condition;
import com.yukthitech.persistence.repository.annotations.DefaultCondition;
import com.yukthitech.persistence.repository.annotations.Field;
import com.yukthitech.persistence.repository.annotations.MethodConditions;
import com.yukthitech.persistence.repository.annotations.OrderBy;
import com.yukthitech.webutils.common.alerts.AlertProcessedDetails;
import com.yukthitech.webutils.common.alerts.PullAlertStatus;
import com.yukthitech.webutils.common.annotations.Optional;
import com.yukthitech.webutils.repository.IWebutilsRepository;

/**
 * Repository for pull alerts.
 * @author akiran
 */
@Optional
public interface IPullAlertRepository extends IWebutilsRepository<PullAlertEntity>
{
	/**
	 * Fetches alerts for specified source. 
	 * @param target target for which alerts to be fetched
	 * @return matching alerts.
	 */
	@OrderBy("id")
	@MethodConditions(conditions = {
		@DefaultCondition(field = "status", value = "NOT_PROCESSED")
		})
	public List<PullAlertEntity> fetchAlerts(@Condition("target") String target);
	
	/**
	 * Fetches non-processed alert with specified dynamic id.
	 * @param target Target for which alert to be fetched.
	 * @param dynamicId dynamic id of alert to be fetched.
	 * @return matching alert.
	 */
	@OrderBy("id")
	@MethodConditions(conditions = {
		@DefaultCondition(field = "status", value = "NOT_PROCESSED")
		})
	public PullAlertEntity fetchAlertByDynamicId(@Condition("target") String target, @Condition("dynamicId") String dynamicId);

	/**
	 * Updates the status of specified alert.
	 * @param id id of alert to update
	 * @param status status to update
	 * @param alertProcessedDetails Alert process details.
	 */
	public void updateStatus(@Condition("id") long id, @Field("status") PullAlertStatus status, @Field("alertProcessedDetails") AlertProcessedDetails alertProcessedDetails);
}
