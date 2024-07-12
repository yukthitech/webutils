package com.yukthitech.webutils.common.lov;

/**
 * Defines the types of LOV supported
 * @author akiran
 */
public enum LovType
{
	/**
	 * Static LOV types, like enum
	 */
	STATIC_TYPE, 
	
	/**
	 * LOV whose list is dynamic and generally obtained from DB 
	 */
	DYNAMIC_TYPE,
	
	/**
	 * Lov whose list comes from common storage.
	 */
	STORED_TYPE;
}
