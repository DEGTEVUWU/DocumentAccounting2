package com.ivan_degtev.documentaccounting2.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.ivan_degtev.documentaccounting2.component.DataInitializer;
import com.ivan_degtev.documentaccounting2.config.EncoderConfig;
import com.ivan_degtev.documentaccounting2.dto.auth.LoginRequestDTO;
import com.ivan_degtev.documentaccounting2.dto.auth.UserRegisterDTO;
import com.ivan_degtev.documentaccounting2.mapper.UserMapper;
import com.ivan_degtev.documentaccounting2.model.Role;
import com.ivan_degtev.documentaccounting2.model.User;
import com.ivan_degtev.documentaccounting2.model.enums.RoleEnum;
import com.ivan_degtev.documentaccounting2.repository.DocumentRepository;
import com.ivan_degtev.documentaccounting2.repository.UserRepository;
import com.ivan_degtev.documentaccounting2.service.RoleService;
import com.ivan_degtev.documentaccounting2.service.UserService;
import com.ivan_degtev.documentaccounting2.utils.UserUtils;
import io.swagger.annotations.Authorization;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openapitools.jackson.nullable.JsonNullableModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static com.ivan_degtev.documentaccounting2.model.enums.RoleEnum.ROLE_ADMIN;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.any;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthControllerTest {
    private final Logger logger = LoggerFactory.getLogger(UserControllerTest.class);
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private DocumentRepository documentRepository;
    @Autowired
    private AuthController authController;
    @Autowired
    private DataInitializer dataInitializer;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private UserService userService;
    @Autowired
    private RoleService roleService;
    private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();


    private UserUtils userUtils;
    private String token;
    private String invalidToken;

    @BeforeEach
    @Transactional
    public void setUpForEach() {
        userRepository.deleteAll();
        logger.info("удалил перед началом теста всех юзеров");
        documentRepository.deleteAll();
        logger.info("удалил перед началом теста все документы");
        authController.logoutUser();
        dataInitializer.run(null);

        logger.info("все юзеры в базе {}", userRepository.findAll().size());

        LoginRequestDTO loginRequestDTO = new LoginRequestDTO("admin", "password");
        Map<String, Object> authResponse = (Map<String, Object>) authController.authenticateUser(loginRequestDTO).getBody();
        token = (String) authResponse.get("jwtToken");
        logger.info("сделал авторизацию через админа и имею токен {}", token);

        logger.info("закинул 1 тестовый документ в базу {}", documentRepository.findAll().size());
    }

    @Test
    @Transactional
    void authenticateUser() throws Exception {
        LoginRequestDTO loginRequestDTO = createLoginRequestDTO();
//        Role testRoleAdmin = new Role();
//        testRoleAdmin.setIdRole(1);
//        testRoleAdmin.setName(RoleEnum.ROLE_ADMIN);
        ObjectMapper objectMapper = createObjectMapper();
        String jsonRequest = objectMapper.writeValueAsString(loginRequestDTO);

        mockMvc.perform(post("/api/auth/sign-in")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.user.username", is("admin")))
                .andExpect(jsonPath("$.user.email", is("diogteff.ivan@yandex.com")))
                .andExpect(jsonPath("$.user.roles[0].idRole", is(1)))
                .andExpect(jsonPath("$.user.roles[0].name", is("ROLE_ADMIN")));
    }

    @Test
    @Transactional
    void registerUser() throws Exception {
        UserRegisterDTO userRegisterDTO = createUserRegisterDTO();
        Role testRoleUser = new Role();
        testRoleUser.setIdRole(3);
        testRoleUser.setName(RoleEnum.ROLE_USER);

        ObjectMapper objectMapper = createObjectMapper();
        String jsonRequest = objectMapper.writeValueAsString(userRegisterDTO);

        mockMvc.perform(post("/api/auth/sign-up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message", is("User registered successfully")));

        User registeredUser = userService.findByUsername(userRegisterDTO.getUsername());
        assertThat(registeredUser).isNotNull();
        assertThat(encoder.matches(userRegisterDTO.getPassword(), registeredUser.getPassword())).isTrue();
        assertThat(registeredUser.getUsername()).isEqualTo(userRegisterDTO.getUsername());
        assertThat(registeredUser.getEmail()).isEqualTo(userRegisterDTO.getEmail());
        assertThat(registeredUser.getName()).isEqualTo(userRegisterDTO.getName());
        assertThat(registeredUser.getRoles()).containsExactlyInAnyOrder(testRoleUser);
        assertThat(registeredUser.getPassword()).isNotEqualTo(userRegisterDTO.getPassword());
    }

    @Test
    void logoutUser() {
    }
    private LoginRequestDTO createLoginRequestDTO() {
        return new LoginRequestDTO("admin", "password");
    }
    private UserRegisterDTO createUserRegisterDTO() {
        UserRegisterDTO userRegisterDTO = new UserRegisterDTO();
        userRegisterDTO.setName("testName");
        userRegisterDTO.setUsername("testUsername");
        userRegisterDTO.setEmail("testEmail@email.com");
        userRegisterDTO.setPassword("testPassword");
        return userRegisterDTO;
    }
    private ObjectMapper createObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule()); // Поддержка Java Time API
        objectMapper.registerModule(new JsonNullableModule()); // Поддержка JsonNullable
        return objectMapper;
    }
}