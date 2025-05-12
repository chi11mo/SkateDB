package com.chillmo.skatedb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
public class SkateDbApplication {

    public static void main(String[] args) {
        SpringApplication.run(SkateDbApplication.class, args);
    }

}
