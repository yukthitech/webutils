package com.webutils.services.lov.stored;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

import com.webutils.lov.LovOption;

@RestController
@RequestMapping("/api/lov/stored")
public class StoredLovController 
{
    @Autowired
    private StoredLovService storedLovService;

    @GetMapping("/options/{lovName}")
    public List<LovOption> getLovOptions(@PathVariable("lovName") String lovName)
    {
        return storedLovService.getLovOptions(lovName);
    }

    @GetMapping("/child-options/{parentLovName}/{parentLovOptionLabel}/{lovName}")
    public List<LovOption> getChildLovOptions(
        @PathVariable("parentLovName") String parentLovName,
        @PathVariable("parentLovOptionLabel") String parentLovOptionLabel, 
        @PathVariable("lovName") String lovName)
    {
        return storedLovService.getChildLovOptions(parentLovName, parentLovOptionLabel, lovName);
    }
}
