package com.ivan_degtev.documentaccounting2.service;


import com.ivan_degtev.documentaccounting2.dto.auth.LoginRequestDTO;
import com.ivan_degtev.documentaccounting2.dto.auth.UserRegisterDTO;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public interface AuthService {
    Map<String, Object> authenticateUser(LoginRequestDTO loginRequestDTO);
    Map<String, Object> registerUser(UserRegisterDTO userRegisterDTO);
    ResponseCookie logoutUser();
}
