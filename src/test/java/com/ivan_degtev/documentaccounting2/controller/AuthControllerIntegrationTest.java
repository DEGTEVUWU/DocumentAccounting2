package com.ivan_degtev.documentaccounting2.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.ivan_degtev.documentaccounting2.component.DataInitializer;
import com.ivan_degtev.documentaccounting2.controller.utils.DependenciesForTests;
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
import com.ivan_degtev.documentaccounting2.utils.JwtUtils;
import com.ivan_degtev.documentaccounting2.utils.UserUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openapitools.jackson.nullable.JsonNullableModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Slf4j
class AuthControllerIntegrationTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private DependenciesForTests dependenciesForTests;
    @Autowired
    private UserService userService;
    private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
    private String token;

    @BeforeEach
    @Transactional
    public void setUpForEach() {
        Map<String, Object> authResponse = dependenciesForTests.initialPreparationOfTablesAndAuthentication();
        token = (String) authResponse.get("jwtToken");
    }

    @Test
    @Transactional
    void authenticateUser() throws Exception {
        LoginRequestDTO loginRequestDTO = createLoginRequestDTO();
        ObjectMapper objectMapper = createObjectMapper();
        String jsonRequest = objectMapper.writeValueAsString(loginRequestDTO);

        mockMvc.perform(post("/api/auth/sign-in")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.user.username", is("admin")))
                .andExpect(jsonPath("$.user.email", is("diogteff.ivan@yandex.com")))
                .andExpect(jsonPath("$.user.roles[?(@.name == 'ROLE_ADMIN')]").exists())
                .andExpect(jsonPath("$.user.roles[?(@.name == 'ROLE_USER')]").exists());
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
    void logoutUser() throws Exception {
        ResponseCookie cleanJwtCookie = ResponseCookie.from("springAuthDemoToken", "")
                .path("/api")
                .build();

        mockMvc.perform(post("/api/auth/sign-out")
                        .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.SET_COOKIE, cleanJwtCookie.toString()))
                .andExpect(content().string("You've been signed out"));
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
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.registerModule(new JsonNullableModule());
        return objectMapper;
    }
}