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

package com.webutils.services.form.lov.stored;

import java.util.Set;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.yukthitech.persistence.annotations.DataType;
import com.yukthitech.persistence.annotations.DataTypeMapping;
import com.yukthitech.persistence.annotations.UniqueConstraint;
import com.yukthitech.persistence.annotations.UniqueConstraints;
import com.yukthitech.persistence.conversion.impl.JsonConverter;

import lombok.Data;

/**
 * Represents store LOV entry.
 * @author akiran
 */
@Data
@UniqueConstraints({
	@UniqueConstraint(name = "UQ_ST_LOV_NAME", fields = {"name"}, finalName = true)
	})
@Table(name = "STORED_LOV")
public class StoredLovEntity
{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "DESCRIPTION", length = 500, nullable = true)
	private String description;

	/**
	 * Name of lov.
	 */
	@Column(name = "NAME", length = 100, nullable = false)
	private String name;

	/**
	 * Flag indicating if authentication is required to access this lov.
	 */
	@Column(name = "AUTH_REQUIRED", nullable = false)
	private boolean authRequired;
	
	/**
	 * Roles which are authorized to access this lov. Defaults to null, which 
	 * means access to all roles.
	 */
	@Column(name = "AUTH_ROLES", nullable = false)
	@DataTypeMapping(type = DataType.STRING, converterType = JsonConverter.class)
	private Set<String> authorizedRoles;
	
	/**
	 * Parent lov if any.
	 */
	@Column(name = "PARENT_LOV_ID", nullable = true)
	@ManyToOne
	private StoredLovEntity parent;
}
