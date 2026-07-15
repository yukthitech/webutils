package com.webutils.testapp.sample;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Generic sample rows for search widget testing.
 */
@Data
@NoArgsConstructor
@Table(name = "SAMPLE_ITEM")
public class SampleItemEntity
{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Long id;

	@Column(name = "NAME", nullable = false, length = 200)
	private String name;

	@Column(name = "CATEGORY", nullable = false, length = 100)
	private String category;

	@Column(name = "STATUS", nullable = false, length = 50)
	private String status;

	@Column(name = "DESCRIPTION", length = 1000)
	private String description;
}
