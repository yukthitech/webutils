package com.yukthi.webutils.common.controllers;

import com.yukthi.webutils.common.LovType;
import com.yukthi.webutils.common.RemoteService;
import com.yukthi.webutils.common.models.LovListResponse;

/**
 * Controller for fetching list of values (LOV).
 * @author akiran
 */
@RemoteService
public interface ILovController extends IClientController<ILovController>
{

	/**
	 * Service method to fetch LOV values.
	 * @param lovName LOV name whose values needs to be fetched
	 * @param type Static or dynamic lov
	 * @return Response hacing list of LOV values
	 */
	public LovListResponse fetchLov(String lovName, LovType type);

	/**
	 * Fetches List of values for the field whose values are dependent on other field. 
	 * This will by default of dynamic type.
	 * 
	 * @param lovName Name of the LOV to fetch.
	 * @param dependencyValue Dependency field value.
	 * @return List of dependenct field LOV based on dependency value.
	 */
	public LovListResponse fetchDependentLov(String lovName, String dependencyValue);
}