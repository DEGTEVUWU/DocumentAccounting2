package com.ivan_degtev.documentaccounting2.controller;

import com.ivan_degtev.documentaccounting2.dto.auth.UserRegisterDTO;
import com.ivan_degtev.documentaccounting2.service.impl.AuthServiceImpl;
import com.ivan_degtev.documentaccounting2.dto.auth.LoginRequestDTO;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("api/auth")
@AllArgsConstructor
@Slf4j
public class AuthController {
    private final AuthServiceImpl authService;

    @PostMapping("/sign-in")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequestDTO loginRequestDTO) {
        Map<String, Object> resultMapWithDataAuthenticate = authService.authenticateUser(loginRequestDTO);
        return ResponseEntity
                .ok()
                .header(HttpHeaders.SET_COOKIE, resultMapWithDataAuthenticate.get("jwtCookie").toString())
                .body(resultMapWithDataAuthenticate);
    }

    @PostMapping("/sign-up")
    public ResponseEntity<?> registerUser(@Valid @RequestBody UserRegisterDTO userRegisterDTO) {
        Map<String, Object> resultMapWithDataAuthenticate = authService.registerUser(userRegisterDTO);
        return new ResponseEntity<>(resultMapWithDataAuthenticate, HttpStatus.CREATED);
    }

    @PostMapping("/sign-out")
    public ResponseEntity<?> logoutUser() {
        ResponseCookie cleanCoolie = authService.logoutUser();
        return ResponseEntity
                .ok()
                .header(HttpHeaders.SET_COOKIE, cleanCoolie.toString())
                .body("You've been signed out");
    }
}
