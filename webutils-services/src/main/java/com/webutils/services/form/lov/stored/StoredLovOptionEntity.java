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

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.webutils.services.user.UserEntity;
import com.yukthitech.persistence.annotations.UniqueConstraint;
import com.yukthitech.persistence.annotations.UniqueConstraints;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * Represents store LOV entry.
 * @author akiran
 */
@Data
@Accessors(chain = true)
@NoArgsConstructor
@UniqueConstraints({
	@UniqueConstraint(name = "UQ_STORED_LOV_OPTION_LBL", fields = {"lov", "label", "parentOptionId"}, finalName = true)
	})
@Table(name = "STORED_LOV_OPTION")
public class StoredLovOptionEntity
{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "LOV_ID", nullable = false)
	@ManyToOne
	private StoredLovEntity lov;

	@Column(name = "PARENT_OPTION", nullable = true)
	private Long parentOptionId;

	@Column(name = "LABEL", length = 500, nullable = false)
	private String label;

	@Column(name = "IS_APPROVED", nullable = false)
	private boolean approved;

	@Column(name = "CREATED_BY_ID", nullable = false)
	private UserEntity createdBy;

	@Column(name = "CREATED_ON", nullable = false)
	private Date createdOn;

	public StoredLovOptionEntity(Long id)
	{
		this.id = id;
	}
}
