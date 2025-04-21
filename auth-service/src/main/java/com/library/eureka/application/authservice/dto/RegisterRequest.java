package com.library.eureka.application.authservice.dto;
import lombok.Data;
import java.time.LocalDate;

@Data
public class RegisterRequest {
    private String username;
    private String password;
    private String fullName;
    private LocalDate birthDate;
}