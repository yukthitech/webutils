package com.webutils.common.search;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Single row in search grid results.
 */
@Data
@NoArgsConstructor
public class SearchRow
{
	private List<String> data;

	public SearchRow(List<String> data)
	{
		this.data = data;
	}

	public void addValue(String value)
	{
		if(data == null)
		{
			data = new ArrayList<>();
		}
		data.add(value);
	}
}
