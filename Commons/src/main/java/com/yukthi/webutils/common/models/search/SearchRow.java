package com.yukthi.webutils.common.models.search;

import java.util.ArrayList;
import java.util.List;

/**
 * Row in search results.
 * @author akiran
 */
public class SearchRow
{
	/**
	 * Data of the row.
	 */
	private List<String> data;
	
	/**
	 * Instantiates a new search row.
	 */
	public SearchRow()
	{}
	
	/**
	 * Instantiates a new search row.
	 *
	 * @param data the data
	 */
	public SearchRow(List<String> data)
	{
		this.data = data;
	}

	/**
	 * Gets the data of the row.
	 *
	 * @return the data of the row
	 */
	public List<String> getData()
	{
		return data;
	}

	/**
	 * Sets the data of the row.
	 *
	 * @param data the new data of the row
	 */
	public void setData(List<String> data)
	{
		this.data = data;
	}
	
	/**
	 * Adds specified value to the current row.
	 * @param value Value to be added.
	 */
	public void addValue(String value)
	{
		if(data == null)
		{
			data = new ArrayList<>();
		}
		
		data.add(value);
	}
}
