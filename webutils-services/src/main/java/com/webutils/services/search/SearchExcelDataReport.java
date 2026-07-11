package com.webutils.services.search;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import com.webutils.common.search.ExecuteSearchResponse;
import com.webutils.common.search.SearchColumn;
import com.yukthitech.excel.exporter.data.Cell;
import com.yukthitech.excel.exporter.data.IExcelDataReport;

/**
 * Converts search grid results into excel export format.
 */
public class SearchExcelDataReport implements IExcelDataReport
{
	private final String name;
	private final List<String> headings;
	private final List<List<Cell>> rows;

	@SuppressWarnings("unused")
	public SearchExcelDataReport(String name, ExecuteSearchResponse response)
	{
		this.name = name;
		List<SearchColumn> searchColumns = response.getSearchColumns();
		headings = searchColumns.stream()
				.filter(SearchColumn::isDisplayable)
				.map(SearchColumn::getHeading)
				.collect(Collectors.toList());

		rows = new LinkedList<>();
		if(response.getSearchResults() != null)
		{
			response.getSearchResults().forEach(searchRow -> {
				AtomicInteger index = new AtomicInteger();
				List<Cell> row = searchRow.getData().stream()
						.filter(val -> searchColumns.get(index.getAndIncrement()).isDisplayable())
						.map(val -> val == null ? new Cell("") : new Cell(val))
						.collect(Collectors.toList());
				rows.add(row);
			});
		}
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
