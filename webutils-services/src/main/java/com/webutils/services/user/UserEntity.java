package com.webutils.services.user;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.yukthitech.persistence.annotations.DataTypeMapping;
import com.yukthitech.persistence.annotations.UniqueConstraint;
import com.yukthitech.persistence.annotations.UniqueConstraints;
import com.yukthitech.persistence.conversion.impl.PasswordEncryptionConverter;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * User entity representing a user
 * 
 * Contains user information including name, email, roles, and audit fields
 */
@Data
@NoArgsConstructor
@Accessors(chain = true)
@Table(name = "USER")
@UniqueConstraints(value = {
    @UniqueConstraint(name = "UQ_IDX_USER_EMAIL_CUSTOM_SPACE", fields = { "email", "customSpace" })
})
public class UserEntity 
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "NAME")
    private String name;

    @Column(name = "EMAIL")
    @UniqueConstraint(name = "UQ_IDX_USER_EMAIL")
    private String email;

	@Column(name = "CUSTOM_SPACE", length = 100, nullable = false)
	private String customSpace = "";

	@DataTypeMapping(converterType = PasswordEncryptionConverter.class)
    @Column(name = "PASSWORD")
    private String password;

    @Column(name = "IS_ACTIVE")
    private boolean active;

    @Column(name = "CREATED_ON")
    private Date createdOn = new Date();

    @Column(name = "UPDATED_ON")
    private Date updatedOn = new Date();

    @ManyToOne
    @Column(name = "CREATED_BY_ID")
    private UserEntity createdBy;

    @ManyToOne
    @Column(name = "UPDATED_BY_ID")
    private UserEntity updatedBy;

    public UserEntity(long id)
    {
        this.id = id;
    }
}
