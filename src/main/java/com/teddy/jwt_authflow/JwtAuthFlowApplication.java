package com.teddy.jwt_authflow;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class JwtAuthFlowApplication {

    public static void main(String[] args) {
        SpringApplication.run(JwtAuthFlowApplication.class, args);
    }

}
