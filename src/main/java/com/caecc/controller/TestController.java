package com.caecc.controller;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;

@RestController
public class TestController {

    @GetMapping("/test")
    public Object test(){
        return "hello world";
    }

}
