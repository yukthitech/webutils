package com.webutils.services.common;

import org.springframework.beans.factory.annotation.Autowired;

import jakarta.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class FileController 
{
    @Autowired
    private FileService fileService;

    @Autowired
    private HttpServletResponse response;

    @GetMapping("/file/{groupName}/{fileName}")
    public void downloadFile(@PathVariable("groupName") String groupName, @PathVariable("fileName") String fileName)
    {
        fileService.writeTo(groupName, fileName, false, response);
    }

    @GetMapping("/image/{groupName}/{fileName}")
    public void downloadImage(@PathVariable("groupName") String groupName, @PathVariable("fileName") String fileName)
    {
        fileService.writeTo(groupName, fileName, true, response);
    }
}
