package com.logistics.logisticsapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
public class LogisticsAppApplication {

    public static void main(String[] args) {
        SpringApplication.run(LogisticsAppApplication.class, args);
    }
}
