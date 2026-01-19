package com.webutils.common.form.model;

import java.util.HashMap;
import java.util.Map;

/**
 * Defines the types of LOV supported
 * @author akiran
 */
public enum LovType
{
	/**
	 * Static LOV types, like enum
	 */
	STATIC_TYPE("static"), 
	
	/**
	 * LOV whose list is obtained from service or repository methods.
	 */
	DYNAMIC_TYPE("dynamic"),
	
	/**
	 * Lov whose list comes from common LOV storage.
	 */
	STORED_TYPE("stored")
	;
	
	private static Map<String, LovType> lovMap;
	
	private String name;
	
	private LovType(String name)
	{
		this.name = name;
	}
	
	public String getName()
	{
		return name;
	}
	
	public static synchronized LovType getLovType(String name)
	{
		if(lovMap != null)
		{
			return lovMap.get(name);
		}
		
		Map<String, LovType> map = new HashMap<String, LovType>();
		
		for(LovType type : LovType.values())
		{
			map.put(type.name, type);
		}
		
		lovMap = map;
		return lovMap.get(name);
	}
}
