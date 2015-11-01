package com.yukthi.webutils.common;

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
	DYNAMIC_TYPE;
}
