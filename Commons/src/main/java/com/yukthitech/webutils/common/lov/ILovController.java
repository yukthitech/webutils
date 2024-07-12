package com.yukthitech.webutils.common.lov;

import com.yukthitech.webutils.common.controllers.IClientController;

/**
 * Controller for fetching list of values (LOV).
 * @author akiran
 */
public interface ILovController extends IClientController<ILovController>
{

	/**
	 * Service method to fetch LOV values.
	 * @param lovName LOV name whose values needs to be fetched
	 * @param type Type of lov
	 * @return Response hacing list of LOV values
	 */
	public LovListResponse fetchLov(String lovName, LovType type);

	/**
	 * Fetches List of values for the field whose values are dependent on other field. 
	 * This will by default of dynamic type.
	 * 
	 * @param lovName Name of the LOV to fetch.
	 * @param type Type of lov
	 * @param dependencyValue Dependency field value.
	 * @return List of dependenct field LOV based on dependency value.
	 */
	public LovListResponse fetchDependentLov(String lovName, LovType type, String dependencyValue);
}