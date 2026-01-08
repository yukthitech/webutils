package com.webutils.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * User details that will be accessible on the request.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDetails
{
	private long id;
	
	private String name;
	
	private String mailId;
	
	private String role;
	
	private String authToken;
}
