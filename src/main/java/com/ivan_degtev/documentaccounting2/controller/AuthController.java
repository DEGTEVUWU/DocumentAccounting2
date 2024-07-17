package com.ivan_degtev.documentaccounting2.controller;

import com.ivan_degtev.documentaccounting2.config.security.UserDetailsImpl;
import com.ivan_degtev.documentaccounting2.dto.auth.UserRegisterDTO;
import com.ivan_degtev.documentaccounting2.mapper.UserMapper;
import com.ivan_degtev.documentaccounting2.model.Role;
import com.ivan_degtev.documentaccounting2.model.User;
import com.ivan_degtev.documentaccounting2.model.enums.RoleEnum;
import com.ivan_degtev.documentaccounting2.service.RoleService;
import com.ivan_degtev.documentaccounting2.service.UserService;
import com.ivan_degtev.documentaccounting2.utils.JwtUtils;
import com.ivan_degtev.documentaccounting2.dto.auth.LoginRequestDTO;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.security.authentication.*;
import org.springframework.security.core.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("api/auth")
@AllArgsConstructor
public class AuthController {
    private static Logger logger = LoggerFactory.getLogger(AuthController.class);
    AuthenticationManager authenticationManager;
    UserService userService;
    RoleService roleService;
    UserMapper userMapper;
    PasswordEncoder encoder;
    JwtUtils jwtUtils;

    @PostMapping("/sign-in")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequestDTO loginRequestDTO) {
        logger.info("зашёл в метод авторизацию, дто щас {}", loginRequestDTO.toString());
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequestDTO.getUsername(), loginRequestDTO.getPassword())
        );
        logger.info("взял аутентификацию {}", authentication.toString());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        logger.info("юзер деталс взят из аутентификации принципал{}", authentication.getPrincipal().toString());
        User user = userService.findByUsername(userDetails.getUsername());
        logger.info("юзер взял из юзер сервиса - репо{}", user.getUsername());
        ResponseCookie jwtCookie = jwtUtils.generateJwtCookie(user.getUsername());
        logger.info("создана кука из ютила именно для авторизованного юзера{}", jwtCookie.toString());
        String jwtToken = jwtCookie.getValue();
        String jwtToken2 = jwtUtils.generateTokenFromUsername(user.getUsername());

        Map<String, Object> response = new HashMap<>();
        response.put("user", user);
        response.put("jwtCookie", jwtCookie);
        response.put("jwtToken", jwtToken);
        response.put("jwtToken2", jwtToken2);
        return ResponseEntity
                .ok()
                .header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
                .body(response);
    }

    @PostMapping("/sign-up")
    public ResponseEntity<?> registerUser(@Valid @RequestBody UserRegisterDTO userRegisterDTO) {
        logger.info("зашел в мметод регистрации");
        User user = userMapper.toUser(userRegisterDTO);
        logger.info("замапил дто в сущность - {}", user.toString());
        user.setPassword(encoder.encode(user.getPassword()));
        logger.info("закодировал пароль");
        Set<Role> roles = new HashSet<>();
        logger.info("создал пустой сет ролей");
        Role userRole = roleService.findByName(RoleEnum.ROLE_USER);
        logger.info("получил роль юзера(по факту это и есть юзер-роль) из роль-сервиса, отдав туда роль-енам-роль юзер {}", userRole.toString());
        roles.add(userRole);
        user.setRoles(roles);
        userService.save(user);
        logger.info("сохранил в репо юзера {}", user.toString());
        Map<String, Object> response = new HashMap<>();
        response.put("message", "User registered successfully");
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/sign-out")
    public ResponseEntity<?> logoutUser() {
        logger.info("зашел в метод разлогиневания");
        ResponseCookie cookie = jwtUtils.getCleanJwtCookie();
        logger.info("сделал чисткю куку {}", cookie.toString());
        return ResponseEntity
                .ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body("You've been signed out");
    }
}
