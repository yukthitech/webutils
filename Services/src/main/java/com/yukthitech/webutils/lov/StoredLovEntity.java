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

import javax.persistence.Column;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.yukthitech.persistence.annotations.NotUpdateable;
import com.yukthitech.persistence.annotations.UniqueConstraint;
import com.yukthitech.persistence.annotations.UniqueConstraints;
import com.yukthitech.webutils.repository.WebutilsTrackedEntity;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Represents store LOV entry.
 * @author akiran
 */
@Data
@EqualsAndHashCode(callSuper = true)
@UniqueConstraints({
	@UniqueConstraint(name = "UQ_ST_LOV_NAME", fields = {"name"}, finalName = true)
	})
@Table(name = "WEBUTILS_STORED_LOV")
public class StoredLovEntity extends WebutilsTrackedEntity
{
	/**
	 * Space identity.
	 */
	@NotUpdateable
	@Column(name = "SPACE_IDENTITY", length = 150, nullable = false)
	private String spaceIdentity = "";

	/**
	 * Name of lov.
	 */
	@Column(name = "NAME", length = 100, nullable = false)
	private String name;

	/**
	 * Used to store extra info that can be used by applications
	 * for authorization and other purposes.
	 */
	@Column(name = "EXTRA_INFO", length = 500, nullable = true)
	private String extraInfo;
	
	/**
	 * Parent lov if any.
	 */
	@Column(name = "PARENT_LOV_ID", nullable = true)
	@ManyToOne
	private StoredLovEntity parent;
	
	/**
	 * Parent lov option id provider.
	 */
	@Column(name = "PARENT_OPT_ID_PROVIDER", length = 150, nullable = true)
	private String parentOptIdProvider;
}
