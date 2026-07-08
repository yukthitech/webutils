package com.webutils.common.search;

import com.webutils.common.form.model.FieldType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Column metadata in search grid results.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SearchColumn
{
	private String name;
	private String heading;
	private boolean displayable;
	private FieldType type;
	private SearchResultType searchResultType;
}
