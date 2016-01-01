/*
 * The MIT License (MIT)
 * Copyright (c) 2016 "Yukthi Techsoft Pvt. Ltd." (http://yukthi-tech.co.in)

 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.yukthi.webutils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.beanutils.PropertyUtils;

import com.yukthi.excel.exporter.data.Cell;
import com.yukthi.excel.exporter.data.IExcelDataReport;
import com.yukthi.utils.exceptions.InvalidStateException;
import com.yukthi.webutils.common.models.def.FieldDef;
import com.yukthi.webutils.common.models.def.ModelDef;

/**
 * Coverts search results into excel date report format. Used during export of search results.
 * 
 * @author akiran
 */
public class SearchExcelDataReport implements IExcelDataReport
{
	/**
	 * Name of the excel report. Used as the tab label in output excel
	 */
	private String name;
	
	private List<String> headings;
	
	private List<List<Cell>> rows;
	
	public SearchExcelDataReport(String name, ModelDef searchResultsDef, List<Object> results)
	{
		this.name = name;
		
		int fieldCount = searchResultsDef.getFields().size();
		
		//compute the headings
		headings = new ArrayList<>(fieldCount);
		
		for(FieldDef field : searchResultsDef.getFields())
		{
			//ignore non displayable fields
			if(!field.isDisplayable())
			{
				continue;
			}
			
			headings.add(field.getLabel());
		}
		
		//compute the rows
		rows = new LinkedList<>();
		List<Cell> row = null;
		Object value = null;
		SimpleDateFormat dateFormat = new SimpleDateFormat(searchResultsDef.getDateFormat());
		
		for(Object bean : results)
		{
			row = new ArrayList<>(fieldCount);
			
			//loop through the fields
			for(FieldDef field : searchResultsDef.getFields())
			{
				//ignore non displayable fields
				if(!field.isDisplayable())
				{
					continue;
				}
				
				//fetch the value of the field for current row
				try
				{
					value = PropertyUtils.getProperty(bean, field.getName());
				}catch(Exception ex)
				{
					throw new InvalidStateException(ex, "An error occurred while fetching field '{}' from bean - {}", field.getName(), bean.getClass().getName());
				}

				value = (value == null) ? "" : value;
				value = (value instanceof Date) ? dateFormat.format(value) : value.toString();
				
				row.add(new Cell((String)value));
			}
			
			rows.add(row);
		}
	}
	
	/* (non-Javadoc)
	 * @see com.yukthi.excel.exporter.data.IExcelDataReport#getName()
	 */
	@Override
	public String getName()
	{
		return name;
	}

	/* (non-Javadoc)
	 * @see com.yukthi.excel.exporter.data.IExcelDataReport#headings()
	 */
	@Override
	public String[] headings()
	{
		return headings.toArray(new String[0]);
	}

	/* (non-Javadoc)
	 * @see com.yukthi.excel.exporter.data.IExcelDataReport#rows()
	 */
	@Override
	public List<List<Cell>> rows()
	{
		return rows;
	}

}
