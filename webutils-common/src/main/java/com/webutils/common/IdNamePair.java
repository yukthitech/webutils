package com.webutils.common;

import com.yukthitech.persistence.repository.annotations.Field;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * A pair of id and name.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class IdNamePair 
{
    @Field("id")    
    private Long id;

    @Field("name")
    private String name;
}
