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

package com.yukthitech.webutils;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import com.yukthitech.excel.exporter.data.Cell;
import com.yukthitech.excel.exporter.data.IExcelDataReport;
import com.yukthitech.webutils.common.models.search.ExecuteSearchResponse;
import com.yukthitech.webutils.common.models.search.SearchColumn;

/**
 * Converts search results into excel date report format. Used during export of search results.
 * 
 * @author akiran
 */
public class SearchExcelDataReport implements IExcelDataReport
{
	/**
	 * Name of the excel report. Used as the tab label in output excel
	 */
	private String name;
	
	/**
	 * Heading of the excel sheet.
	 */
	private List<String> headings;
	
	/**
	 * Data in excel sheet.
	 */
	private List<List<Cell>> rows;
	
	/**
	 * Instantiates a new search excel data report.
	 *
	 * @param name the name
	 * @param response the response
	 */
	public SearchExcelDataReport(String name, ExecuteSearchResponse response)
	{
		this.name = name;
		
		List<SearchColumn> searchColumns = response.getSearchColumns();
		
		//compute the headings
		headings = searchColumns.stream()
			.filter(srchCol -> srchCol.isDisplayable())
			.map(srchCol -> srchCol.getHeading())
			.collect(Collectors.toList());
		
		//compute the rows
		rows = new LinkedList<>();
		
		response.getSearchResults().stream().forEach(searchRow -> {
			AtomicInteger index = new AtomicInteger();
			
			List<Cell> row = searchRow.getData().stream()
				.filter( val -> searchColumns.get(index.getAndIncrement()).isDisplayable() )
				.map(val -> (val == null) ? new Cell("") : new Cell(val))
				.collect(Collectors.toList());
			
			rows.add(row);
		});
	}
	
	@Override
	public String getName()
	{
		return name;
	}

	@Override
	public String[] headings()
	{
		return headings.toArray(new String[0]);
	}

	@Override
	public List<List<Cell>> rows()
	{
		return rows;
	}
}
