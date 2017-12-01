package com.yukthitech.webutils.services.freemarker;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.yukthitech.webutils.services.freemarker.FreemarkerMethod;

/**
 * Simple util functions for free marker testing.
 * @author akiran
 */
public class UtilsFunctions
{
	/**
	 * Sum.
	 *
	 * @param arr the arr
	 * @return the int
	 */
	@FreemarkerMethod
	public static int sum(int... arr)
	{
		int total = 0;
		
		if(arr != null)
		{
			for(int i = 0; i < arr.length; i++)
			{
				total += arr[i];
			}
		}
		
		return total;
	}
	
	/**
	 * Min.
	 *
	 * @param first the first
	 * @param others the others
	 * @return the int
	 */
	@FreemarkerMethod("min")
	public static double minimum(double first, double... others)
	{
		double min = first;
		
		if(others != null)
		{
			for(int i = 0; i < others.length; i++)
			{
				if(min > others[i])
				{
					min = others[i];
				}
			}
		}
		
		return min;
	}
	
	/**
	 * Now.
	 *
	 * @param format the format
	 * @return the string
	 */
	@FreemarkerMethod
	public static String now(String format)
	{
		SimpleDateFormat dataFormat = new SimpleDateFormat(format);
		return dataFormat.format(new Date());
	}
	
	/**
	 * Random.
	 *
	 * @return the int
	 */
	@FreemarkerMethod
	public static int random()
	{
		return (int) (100 * Math.random());
	}
}
