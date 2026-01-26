package com.webutils.services.user;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.yukthitech.persistence.annotations.DataType;
import com.yukthitech.persistence.annotations.DataTypeMapping;
import com.yukthitech.persistence.conversion.impl.JsonConverter;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * User preference entity representing a user preference in the Acharya educational platform
 * 
 * Contains user preference information including key, value, and audit fields
 */
@Data
@NoArgsConstructor
@Table(name = "USER_PREFERENCES")
public class UserPreferenceEntity 
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @ManyToOne
    @Column(name = "USER_ID")
    private UserEntity user;

    @Column(name = "PREF_KEY")
    private String key;

    @Column(name = "PREF_VALUE")
    @DataTypeMapping(type = DataType.STRING, converterType = JsonConverter.class)
    private Object value;

    @Column(name = "LAST_UPDATED_TIME")
    private Date lastUpdatedTime = new Date();
}
