package com.yukthitech.webutils.common.models.def;

import java.io.Serializable;

/**
 * Used to store color value.
 * @author akiran
 */
public class Color implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	/**
	 * Color argb value.
	 */
	private int argb;

	public int getArgb()
	{
		return argb;
	}

	public void setArgb(int argb)
	{
		this.argb = argb;
	}
}
