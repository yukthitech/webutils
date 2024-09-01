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
	 * LOV whose list is obtained from service or repository methods.
	 */
	DYNAMIC_TYPE,
	
	/**
	 * Lov whose list comes from common LOV storage.
	 */
	STORED_TYPE;
}
