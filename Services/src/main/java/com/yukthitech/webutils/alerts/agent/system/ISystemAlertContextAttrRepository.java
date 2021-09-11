package com.yukthitech.webutils.alerts.agent.system;

import java.util.Date;

import com.yukthitech.persistence.repository.annotations.Condition;
import com.yukthitech.persistence.repository.annotations.Field;
import com.yukthitech.persistence.repository.annotations.Operator;
import com.yukthitech.webutils.common.annotations.Optional;
import com.yukthitech.webutils.repository.IWebutilsRepository;

/**
 * Repository for pull alerts.
 * @author akiran
 */
@Optional
public interface ISystemAlertContextAttrRepository extends IWebutilsRepository<SystemAlertContextEntity>
{
	/**
	 * Fetches attribute value with specified name.
	 * @param name name of attribute to fetch
	 * @return matching value
	 */
	@Field("value")
	public Object fetchValue(@Condition("name") String name);
	
	/**
	 * Updates the specified attribute with specified value.
	 * @param name name of attribute to update
	 * @param value value to set
	 * @param date date on which this attributed is getting updated
	 * @return true if attribute is present and is updated.
	 */
	public boolean updateValue(@Condition("name") String name, @Field("value") Object value, @Field("updatedOn") Date date);
	
	/**
	 * Updates the access time of attribute. To indicate attribute is active.
	 * @param name name of attribute to update.
	 * @param date date to update.
	 */
	public void updateAccessTime(@Condition("name") String name, @Field("updatedOn") Date date);
	
	/**
	 * Deletes attribute with specified name.
	 * @param name name of attribute to remove.
	 */
	public void deleteAttribute(@Condition("name") String name);

	/**
	 * Deletes attributes whose updated-on date is less than specified date.
	 * @param olderThan date to compare with
	 * @return number of records deleted.
	 */
	public int deleteOldAttributes(@Condition(value = "updatedOn", op = Operator.LE) Date olderThan);
}
