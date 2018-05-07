package com.yukthitech.webutils.common.cache;

import com.yukthitech.webutils.common.controllers.IClientController;
import com.yukthitech.webutils.common.models.BaseResponse;

/**
 * Controller for access cache related functionalities.
 * @author akiran
*/
public interface ICacheController extends IClientController<ICacheController>
{
	/**
	 * Clears the temp cache.
	 * @return success or failure response.
	 */
	public BaseResponse clearCache();
}
