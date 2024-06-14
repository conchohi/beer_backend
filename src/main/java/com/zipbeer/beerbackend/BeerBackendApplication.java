package com.zipbeer.beerbackend;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.TimeZone;

@SpringBootApplication
public class BeerBackendApplication {
    public static void main(String[] args) {
        SpringApplication.run(BeerBackendApplication.class, args);
    }
    @PostConstruct
    public void init() {
        // timezone 설정
        TimeZone.setDefault(TimeZone.getTimeZone("KST"));
    }
}
