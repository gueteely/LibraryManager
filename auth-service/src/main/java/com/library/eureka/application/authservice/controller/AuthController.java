package com.library.eureka.application.authservice.controller;

import com.library.eureka.application.authservice.model.User;
import com.library.eureka.application.authservice.security.JwtUtil;
import com.library.eureka.application.authservice.service.UserService;
import com.library.eureka.application.authservice.dto.AuthRequest;
import com.library.eureka.application.authservice.dto.AuthResponse;
import com.library.eureka.application.authservice.dto.RegisterRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final JwtUtil jwtUtil;

    public AuthController(AuthenticationManager authenticationManager, UserService userService, JwtUtil jwtUtil) {
        this.authenticationManager = authenticationManager;
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest authRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword())
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        final User user = (User) authentication.getPrincipal();
        final String jwt = jwtUtil.generateToken(user);

        return ResponseEntity.ok(new AuthResponse(jwt, user.getId()));
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest registerRequest) {
        User user1 = new User();
        user1.setUsername(registerRequest.getUsername());
        user1.setPassword(registerRequest.getPassword());
        user1.setFullName(registerRequest.getFullName());
        user1.setBirthDate(registerRequest.getBirthDate());

        User createdUser = userService.createUser(user1);
        return ResponseEntity.ok(createdUser);
    }
}