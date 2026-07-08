package com.webutils.services.search;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Version;

import com.webutils.common.search.SearchSettingsColumn;
import com.webutils.services.user.UserEntity;
import com.yukthitech.persistence.annotations.DataType;
import com.yukthitech.persistence.annotations.DataTypeMapping;
import com.yukthitech.persistence.annotations.NotUpdateable;
import com.yukthitech.persistence.annotations.UniqueConstraint;
import com.yukthitech.persistence.annotations.UniqueConstraints;
import com.yukthitech.persistence.conversion.impl.JsonWithTypeConverter;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Table(name = "WEBUTILS_SEARCH_SETTINGS")
@UniqueConstraints({
	@UniqueConstraint(name = "UQ_USER_QUERY_NAME", fields = {"user", "searchQueryName"}, finalName = false)
})
public class SearchSettingsEntity
{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Long id;

	@Version
	@Column(name = "VERSION")
	private Integer version = 1;

	@NotUpdateable
	@ManyToOne
	@Column(name = "USER_ID", nullable = false)
	private UserEntity user;

	@NotUpdateable
	@Column(name = "SEARCH_QUERY_NAME", nullable = false, length = 100)
	private String searchQueryName;

	@Column(name = "SEARCH_COLUMNS", nullable = false)
	@DataTypeMapping(type = DataType.CLOB, converterType = JsonWithTypeConverter.class)
	private List<SearchSettingsColumn> searchColumns;

	@Column(name = "PAGE_SIZE", nullable = false)
	private Integer pageSize;

	@Column(name = "CREATED_ON", nullable = false)
	private Date createdOn = new Date();

	@Column(name = "UPDATED_ON", nullable = false)
	private Date updatedOn = new Date();

	@ManyToOne
	@Column(name = "CREATED_BY_ID")
	private UserEntity createdBy;

	@ManyToOne
	@Column(name = "UPDATED_BY_ID")
	private UserEntity updatedBy;
}
