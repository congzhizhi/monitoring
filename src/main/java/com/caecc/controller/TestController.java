package com.caecc.controller;


import com.caecc.service.WorkParamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;

@RestController
public class TestController {


    @Autowired
    WorkParamService workParamService;
    @GetMapping("/test")
    public Object test(){
        return workParamService.getAll();
    }

}
