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

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * Represents store LOV entry.
 * @author akiran
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
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
}
