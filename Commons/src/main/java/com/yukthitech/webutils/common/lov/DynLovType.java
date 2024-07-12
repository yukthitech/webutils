package com.yukthitech.webutils.common.lov;

import com.yukthitech.persistence.repository.annotations.Field;

/**
 * Types of dyn LOV types, that can be used in annotation. This is same as LovType
 * but created for annotation {@link Field} to limit lov types that can be specified.
 * @author akiran
 */
public enum DynLovType
{
	/**
	 * LOV whose list is dynamic and generally obtained from DB 
	 */
	DYNAMIC_TYPE,
	
	/**
	 * Lov whose list comes from common storage.
	 */
	STORED_TYPE;
}
