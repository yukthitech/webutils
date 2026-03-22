package com.webutils.common;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class FileInfo
{
	private long ownerId;
	
	private String groupName;
	private String fileName;
}
