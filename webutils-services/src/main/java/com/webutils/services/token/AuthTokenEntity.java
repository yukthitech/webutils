package com.webutils.services.token;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.webutils.services.user.UserEntity;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Table(name = "AUTH_TOKEN")
public class AuthTokenEntity 
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "TOKEN", nullable = false)
    private String token;

    @ManyToOne
    @Column(name = "USER_ID", nullable = false)
    private UserEntity user;

    @Column(name = "CUSTOM_SPACE", nullable = false)
    private String customSpace;

    @Column(name = "EXPIRES_AT", nullable = false)
    private Date expiresAt;

    @Column(name = "CREATED_ON", nullable = false)
    private Date createdOn;

    @Column(name = "LAST_UPDATED_ON", nullable = false)
    private Date lastUpdatedOn;
}
