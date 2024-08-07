package com.ivan_degtev.documentaccounting2.service.impl;

import com.ivan_degtev.documentaccounting2.config.security.UserDetailsImpl;
import com.ivan_degtev.documentaccounting2.dto.auth.LoginRequestDTO;
import com.ivan_degtev.documentaccounting2.dto.auth.UserRegisterDTO;
import com.ivan_degtev.documentaccounting2.mapper.UserMapper;
import com.ivan_degtev.documentaccounting2.model.Role;
import com.ivan_degtev.documentaccounting2.model.User;
import com.ivan_degtev.documentaccounting2.model.enums.RoleEnum;
import com.ivan_degtev.documentaccounting2.service.AuthService;
import com.ivan_degtev.documentaccounting2.service.RoleService;
import com.ivan_degtev.documentaccounting2.utils.JwtUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Service
@AllArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {
    private AuthenticationManager authenticationManager;
    private UserServiceImpl userService;
    private JwtUtils jwtUtils;
    private UserMapper userMapper;
    private PasswordEncoder encoder;
    private RoleService roleService;

    /**
     * @param loginRequestDTO
     * в методе создается аутентификация и наполняется данными из dto, далее она помещается в контекст секьюрити
     * @return мапа с объектами аутентификации(юзернейм, куки, jwt)
     */
    @Override
    @Transactional(transactionManager = "transactionManager")
    public Map<String, Object> authenticateUser(LoginRequestDTO loginRequestDTO) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequestDTO.getUsername(), loginRequestDTO.getPassword())
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        User user = userService.findByUsername(userDetails.getUsername());
        ResponseCookie jwtCookie = jwtUtils.generateJwtCookie(user.getUsername());
        String jwtToken = jwtCookie.getValue();

        Map<String, Object> response = new HashMap<>();
        response.put("user", user);
        response.put("jwtCookie", jwtCookie);
        response.put("jwtToken", jwtToken);
        return response;
    }


    /**
     * @param userRegisterDTO
     * в методе регистрируется юзер, добавляется роль Юзера, сохраняется в БД
     * @return мапа с ответом об успехе
     */
    @Override
    @Transactional(transactionManager = "transactionManager")
    public Map<String, Object> registerUser(UserRegisterDTO userRegisterDTO) {
        User user = userMapper.toUser(userRegisterDTO);
        user.setPassword(encoder.encode(user.getPassword()));
        user.setCreationDate(LocalDate.now());
        Set<Role> roles = new HashSet<>();
        Role userRole = roleService.findByName(RoleEnum.ROLE_USER);
        roles.add(userRole);
        user.setRoles(roles);
        userService.save(user);
        Map<String, Object> response = new HashMap<>();
        response.put("message", "User registered successfully");
        return response;
    }

    @Override
    @Transactional(transactionManager = "transactionManager")
    public ResponseCookie logoutUser() {
        return jwtUtils.getCleanJwtCookie();
    }
}
