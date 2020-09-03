package com.caecc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;


@SpringBootApplication
@ServletComponentScan
@EnableTransactionManagement//开启事务管理
public class BootApp {
    //http://localhost:8000/druid/index.html
    public static void main(String[] args) {
        SpringApplication.run(BootApp.class, args);
    }

}

