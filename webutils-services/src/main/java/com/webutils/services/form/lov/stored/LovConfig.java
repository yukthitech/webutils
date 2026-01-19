package com.webutils.services.form.lov.stored;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class LovConfig 
{
    private boolean saveMissingOptions = false;
    private boolean requireApproval = false;

    private String parentOptionLabel;
}
