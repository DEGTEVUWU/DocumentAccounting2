package com.ivan_degtev.documentaccounting2.controller.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.ivan_degtev.documentaccounting2.component.DataInitializer;
import com.ivan_degtev.documentaccounting2.controller.AuthController;
import com.ivan_degtev.documentaccounting2.dto.auth.LoginRequestDTO;
import com.ivan_degtev.documentaccounting2.dto.auth.UserRegisterDTO;
import com.ivan_degtev.documentaccounting2.repository.DocumentRepository;
import com.ivan_degtev.documentaccounting2.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.openapitools.jackson.nullable.JsonNullableModule;
import org.springframework.stereotype.Service;
import org.springframework.test.context.ActiveProfiles;

import java.util.Map;

@Service
@AllArgsConstructor
@Slf4j
@ActiveProfiles("test")
public class DependenciesForTests {
    private AuthController authController;
    private UserRepository userRepository;
    private DocumentRepository documentRepository;
    private DataInitializer dataInitializer;

    public ObjectMapper createObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule()); // Поддержка Java Time API
        objectMapper.registerModule(new JsonNullableModule()); // Поддержка JsonNullable
        return objectMapper;
    }

    /**
     * тех метод, для регистрации юзера с ролью Юзер
     */
    public void registerAsUser() {
        UserRegisterDTO userRegisterDTO = new UserRegisterDTO();
        userRegisterDTO.setEmail("test@email.com");
        userRegisterDTO.setUsername("user");
        userRegisterDTO.setName("name");
        userRegisterDTO.setPassword("1234");
        authController.registerUser(userRegisterDTO);

    }

    /**
     * тех метод, для авторизации юзера с ролью Юзер
     */
    public String loginAsUser() {
        LoginRequestDTO loginRequestDTO = new LoginRequestDTO("user", "1234");
        var response = authController.authenticateUser(loginRequestDTO);
        String userToken = ((Map<String, String>) response.getBody()).get("jwtToken");
        return userToken;

    }

    /**
     * тех метод, для первичной подготовки таблиц(удаление сатрых записей) и авторизации под юзером с ролью Админ
     */
    public Map<String, Object> initialPreparationOfTablesAndAuthentication() {
        userRepository.deleteAll();
        documentRepository.deleteAll();
        authController.logoutUser();
        dataInitializer.run(null);

        LoginRequestDTO loginRequestDTO = new LoginRequestDTO("admin", "password");
        return (Map<String, Object>) authController.authenticateUser(loginRequestDTO).getBody();
    }
}
