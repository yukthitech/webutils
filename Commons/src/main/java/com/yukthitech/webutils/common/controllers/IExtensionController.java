package com.yukthitech.webutils.common.controllers;

import com.yukthitech.webutils.common.models.BaseResponse;
import com.yukthitech.webutils.common.models.BasicSaveResponse;
import com.yukthitech.webutils.common.models.ExtensionFieldModel;
import com.yukthitech.webutils.common.models.ExtensionFieldReadResponse;
import com.yukthitech.webutils.common.models.ExtensionFieldsResponse;

public interface IExtensionController extends IClientController<IExtensionController>
{

	/**
	 * Fetches extension fields for specified extension name (for current or request specific owner).
	 * @param extensionName Name of the entity extension
	 * @return Response holding extension fields
	 */
	ExtensionFieldsResponse fetchExtensionFields(String extensionName);

	/**
	 * Saves the extension field with specified name. If no extension exists, a new extension gets created
	 * @param extensionField Extension field model
	 * @return Save response with id
	 */
	BasicSaveResponse saveExtensionField(ExtensionFieldModel extensionField);

	/**
	 * Updates specified extension field under specified extension name.
	 * @param extensionField Field to be updated
	 * @return Success/failure message
	 */
	BaseResponse updateExtensionField(ExtensionFieldModel extensionField);

	/**
	 * Deletes extension field with specified id.
	 * @param fieldId Extension field id to be deleted
	 * @return Success/failure response
	 */
	BaseResponse deleteExtensionField(String extensionName, long fieldId);

	/**
	 * API to read extension field for specified extension with specified id.
	 * @param extensionName Extension name under which field needs to be fetched
	 * @param fieldId Id of the extension field to be fetched
	 * @return response with extension field model
	 */
	ExtensionFieldReadResponse readExtensionField(String extensionName, long fieldId);

	/**
	 * Deletes all extension fields of all extensions. Expected to be used for cleanup by test cases. 
	 * @return Success/failure message.
	 */
	BaseResponse deleteAllExtensionFields();

}