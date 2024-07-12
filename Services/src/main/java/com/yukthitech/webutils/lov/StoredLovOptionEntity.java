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

package com.yukthitech.webutils.lov;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.yukthitech.persistence.annotations.DataType;
import com.yukthitech.persistence.annotations.DataTypeMapping;
import com.yukthitech.persistence.annotations.NotUpdateable;
import com.yukthitech.persistence.annotations.UniqueConstraint;
import com.yukthitech.persistence.annotations.UniqueConstraints;
import com.yukthitech.webutils.repository.WebutilsBaseEntity;
import com.yukthitech.webutils.user.UserEntity;

/**
 * Represents store LOV entry.
 * @author akiran
 */
@UniqueConstraints({
	@UniqueConstraint(name = "UQ_ST_LOV_OPT_LBL", fields = {"parentLov", "parentLovOption", "label"}, finalName = true)
	})
@Table(name = "WEBUTILS_STORED_LOV_OPTION")
public class StoredLovOptionEntity extends WebutilsBaseEntity
{
	
	/**
	 * The parent lov.
	 */
	@ManyToOne
	@Column(name = "PARENT_LOV_ID", nullable = false)
	private StoredLovEntity parentLov;
	
	/**
	 * Parent lov option. Eg: Parent state of a city.
	 */
	@ManyToOne
	@Column(name = "PARENT_LOV_OPTION_ID", nullable = true)
	private StoredLovOptionEntity parentLovOption;
	
	/**
	 * Label of lob.
	 */
	@Column(name = "LABEL", length = 500, nullable = false)
	private String label;

	/**
	 * Created by user.
	 */
	@NotUpdateable
	@ManyToOne
	@Column(name = "CREATED_BY_ID")
	private UserEntity createdBy;
	
	/**
	 * The approved.
	 */
	@Column(name = "IS_APPROVED", nullable = false)
	private boolean approved;
	
	/**
	 * Created on time.
	 */
	@NotUpdateable
	@Column(name = "CREATED_ON", nullable = false)
	@DataTypeMapping(type = DataType.DATE_TIME)
	private Date createdOn = new Date();

	/**
	 * Approving user.
	 */
	@ManyToOne
	@Column(name = "APPROVED_BY_ID")
	private UserEntity approvedBy;
	
	/**
	 * Approved on.
	 */
	@Column(name = "APPROVED_ON", nullable = true)
	@DataTypeMapping(type = DataType.DATE_TIME)
	private Date approvedOn = new Date();

	/**
	 * Gets the parent lov.
	 *
	 * @return the parent lov
	 */
	public StoredLovEntity getParentLov()
	{
		return parentLov;
	}

	/**
	 * Sets the parent lov.
	 *
	 * @param parentLov the new parent lov
	 */
	public void setParentLov(StoredLovEntity parentLov)
	{
		this.parentLov = parentLov;
	}
	
	/**
	 * Gets the parent lov option. Eg: Parent state of a city.
	 *
	 * @return the parent lov option
	 */
	public StoredLovOptionEntity getParentLovOption()
	{
		return parentLovOption;
	}

	/**
	 * Sets the parent lov option. Eg: Parent state of a city.
	 *
	 * @param parentLovOption the new parent lov option
	 */
	public void setParentLovOption(StoredLovOptionEntity parentLovOption)
	{
		this.parentLovOption = parentLovOption;
	}

	/**
	 * Gets the label of lob.
	 *
	 * @return the label of lob
	 */
	public String getLabel()
	{
		return label;
	}

	/**
	 * Sets the label of lob.
	 *
	 * @param label the new label of lob
	 */
	public void setLabel(String label)
	{
		this.label = label;
	}

	/**
	 * Gets the created by user.
	 *
	 * @return the created by user
	 */
	public UserEntity getCreatedBy()
	{
		return createdBy;
	}

	/**
	 * Sets the created by user.
	 *
	 * @param createdBy the new created by user
	 */
	public void setCreatedBy(UserEntity createdBy)
	{
		this.createdBy = createdBy;
	}

	/**
	 * Checks if is approved.
	 *
	 * @return true, if is approved
	 */
	public boolean isApproved()
	{
		return approved;
	}

	/**
	 * Sets the approved.
	 *
	 * @param approved the new approved
	 */
	public void setApproved(boolean approved)
	{
		this.approved = approved;
	}

	/**
	 * Gets the created on time.
	 *
	 * @return the created on time
	 */
	public Date getCreatedOn()
	{
		return createdOn;
	}

	/**
	 * Sets the created on time.
	 *
	 * @param createdOn the new created on time
	 */
	public void setCreatedOn(Date createdOn)
	{
		this.createdOn = createdOn;
	}

	/**
	 * Gets the approving user.
	 *
	 * @return the approving user
	 */
	public UserEntity getApprovedBy()
	{
		return approvedBy;
	}

	/**
	 * Sets the approving user.
	 *
	 * @param approvedBy the new approving user
	 */
	public void setApprovedBy(UserEntity approvedBy)
	{
		this.approvedBy = approvedBy;
	}

	/**
	 * Gets the approved on.
	 *
	 * @return the approved on
	 */
	public Date getApprovedOn()
	{
		return approvedOn;
	}

	/**
	 * Sets the approved on.
	 *
	 * @param approvedOn the new approved on
	 */
	public void setApprovedOn(Date approvedOn)
	{
		this.approvedOn = approvedOn;
	}
}
