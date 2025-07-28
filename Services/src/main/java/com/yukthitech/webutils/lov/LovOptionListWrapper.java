package com.yukthitech.webutils.lov;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Used to expose list of options based on label or id.
 */
class LovOptionListWrapper
{
	private Map<String, StoredLovOptionEntity> labelMap = new HashMap<>();
	
	private Map<Long, StoredLovOptionEntity> idMap = new HashMap<>();

	public LovOptionListWrapper(List<StoredLovOptionEntity> lovList)
	{
		lovList.forEach(lov -> addLovOption(lov));
	}
	
	public void addLovOption(StoredLovOptionEntity lov)
	{
		labelMap.put(lov.getLabel().toLowerCase(), lov);
		idMap.put(lov.getId(), lov);
	}
	
	public StoredLovOptionEntity getLovOptionByLabel(String label)
	{
		return labelMap.get(label.toLowerCase());
	}
	
	public StoredLovOptionEntity getById(long id)
	{
		return idMap.get(id);
	}
}
