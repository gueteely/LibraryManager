package com.library.eureka.application.bookservice.client;
import com.library.eureka.application.dto.LogEntryDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "logging-service")
public interface LoggingServiceClient {

    @PostMapping("/logs")
    LogEntryDto createLog(@RequestBody LogEntryDto logEntry);
}