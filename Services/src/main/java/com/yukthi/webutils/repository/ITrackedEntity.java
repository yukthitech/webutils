/*
 * The MIT License (MIT)
 * Copyright (c) 2015 "Yukthi Techsoft Pvt. Ltd." (http://yukthi-tech.co.in)

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

package com.yukthi.webutils.repository;

import java.util.Date;

/**
 * @author akiran
 *
 */
public interface ITrackedEntity
{
	/**
	 * Gets the created on date.
	 *
	 * @return the created on date
	 */
	public Date getCreatedOn();

	/**
	 * Sets the created on date.
	 *
	 * @param createdOn the new created on date
	 */
	public void setCreatedOn(Date createdOn);

	/**
	 * Gets the created By.
	 *
	 * @return the created By
	 */
	public long getCreatedBy();
	
	/**
	 * Sets the created By.
	 *
	 * @param createdBy the new created By
	 */
	public void setCreatedBy(long createdBy);

	/**
	 * Gets the created on date.
	 *
	 * @return the created on date
	 */
	public Date getUpdatedOn();

	/**
	 * Sets the created on date.
	 *
	 * @param updatedOn the new created on date
	 */
	public void setUpdatedOn(Date updatedOn);

	/**
	 * Gets the created By.
	 *
	 * @return the created By
	 */
	public long getUpdatedBy();

	/**
	 * Sets the created By.
	 *
	 * @param updatedBy the new created By
	 */
	public void setUpdatedBy(long updatedBy);
}
