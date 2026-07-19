package com.webutils.testapp.lov;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * Persisted LOV demo form rows (one per submit).
 */
@Getter
@Setter
@NoArgsConstructor
@Accessors(chain = true)
@Table(name = "TEMP_TABLE")
public class TempTableEntity
{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Long id;

	@Column(name = "CATEGORY", nullable = true, length = 100)
	private String category;
}
