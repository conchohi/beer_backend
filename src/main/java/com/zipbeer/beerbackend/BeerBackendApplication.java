package com.zipbeer.beerbackend;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.TimeZone;

@SpringBootApplication
public class BeerBackendApplication {
    public static void main(String[] args) {
        SpringApplication.run(BeerBackendApplication.class, args);
    }
    @Bean
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setMessageConverters(Collections.singletonList(new StringHttpMessageConverter()));
        return restTemplate;
    }
}
