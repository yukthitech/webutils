package com.webutils.common.search;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Maps a search settings column to entity/result field names.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SearchField
{
	private String field;
	private String propertyName;
}
