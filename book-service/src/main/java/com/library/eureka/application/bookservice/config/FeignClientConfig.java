package com.library.eureka.application.bookservice.config;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableFeignClients(basePackages = "com.library.eureka.application.bookservice.client")
public class FeignClientConfig {
}