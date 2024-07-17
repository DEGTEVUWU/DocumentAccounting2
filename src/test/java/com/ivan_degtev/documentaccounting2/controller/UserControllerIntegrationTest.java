package com.ivan_degtev.documentaccounting2.controller;

import com.ivan_degtev.documentaccounting2.component.DataInitializer;
import com.ivan_degtev.documentaccounting2.dto.auth.LoginRequestDTO;
import com.ivan_degtev.documentaccounting2.dto.user.UpdateUserDTOForAdmin;
import com.ivan_degtev.documentaccounting2.dto.user.UpdateUserDTOForUser;
import com.ivan_degtev.documentaccounting2.model.Document;
import com.ivan_degtev.documentaccounting2.model.TypeDocument;
import com.ivan_degtev.documentaccounting2.model.User;
import com.ivan_degtev.documentaccounting2.repository.DocumentRepository;
import com.ivan_degtev.documentaccounting2.repository.UserRepository;
import com.ivan_degtev.documentaccounting2.utils.UserUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openapitools.jackson.nullable.JsonNullable;
import org.openapitools.jackson.nullable.JsonNullableModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;


import java.util.Map;
import java.util.Set;

import static com.ivan_degtev.documentaccounting2.model.enums.TypeDocumentEnum.NOTE;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class UserControllerIntegrationTest {
    private final Logger logger = LoggerFactory.getLogger(UserControllerIntegrationTest.class);
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
    private UserUtils userUtils;
    private String token;


    @BeforeEach
    @Transactional
    public void setUpForEach() {
        userRepository.deleteAll();
        documentRepository.deleteAll();
        authController.logoutUser();
        dataInitializer.run(null);

        userRepository.save(new User("username1", "name1", "email1@email.com", "1234"));
        userRepository.save(new User("username2", "name2", "email2@email.com", "1234"));
        userRepository.save(new User("username3", "name3", "email3@email.com", "1234"));
        logger.info("все юзеры в базе {}", userRepository.findAll().size());

        LoginRequestDTO loginRequestDTO = new LoginRequestDTO("admin", "password");
        Map<String, Object> authResponse = (Map<String, Object>) authController.authenticateUser(loginRequestDTO).getBody();
        token = (String) authResponse.get("jwtToken");
        logger.info("сделал авторизацию через админа и имею токен {}", token);

        createTestDocuments();
        logger.info("закинул 1 тестовый документ в базу {}", documentRepository.findAll().size());
    }

    @Test
    void index() throws Exception {
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(5)));
    }

    @Test
    void getCurrentUser() throws Exception {
        mockMvc.perform(get("/api/users/current-user"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username", is("admin")));

    }

    @Test
    void checkCurrentUserIsAuthor() throws Exception {
        Long documentId = documentRepository.findAll().get(0).getId();
        mockMvc.perform(get("/api/users/check-current-user-is-author/{documentId}", documentId))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }


    @Test
    void show() throws Exception {
        Long userIdToShow = userRepository.findByUsername("admin").get().getIdUser();

        mockMvc.perform(get("/api/users/{userId}", userIdToShow))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username", is("admin")));
    }

    @Test
    void updateForUser_ShouldReturnUpdatedUser_WhenValidData() throws Exception {
        Long userIdToUpdate = findCurrentUserId();
        logger.info("айди админа в данной итерации = {}", userIdToUpdate);

        UpdateUserDTOForUser updateUserDTO = createTestValidUserUpdateDto();

        logger.info("имею юзера в базе {}", userRepository.findByUsername("admin"));
        ObjectMapper objectMapper = createObjectMapper();
        String jsonRequest = objectMapper.writeValueAsString(updateUserDTO);

        mockMvc.perform(put("/api/users/{id}", userIdToUpdate)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username", is("newUsername")))
                .andExpect(jsonPath("$.name", is("newName")))
                .andExpect(jsonPath("$.email", is("newemail@example.com")));
    }
//    @Test
//    void updateForUser_ShouldReturnBadRequest_WhenInvalidData() throws Exception {
//        Long userIdToUpdate = findCurrentUserId();
//        logger.info("айди админа в данной итерации = {}", userIdToUpdate);
//
//        UpdateUserDTOForUser updateUserDTO = createTestNotValidUserUpdateDto();
//        ObjectMapper objectMapper = createObjectMapper();
//        String jsonRequest = objectMapper.writeValueAsString(updateUserDTO);
//        mockMvc.perform(put("/api/users/{id}", userIdToUpdate)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(jsonRequest)
//                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
//                .andExpect(status().isBadRequest());
//    }


    @Test
    void updateForAdmin() throws Exception {
        Long userIdToUpdate = findCurrentUserId();
        logger.info("айди админа в данной итерации = {}", userIdToUpdate);

        UpdateUserDTOForAdmin updateUserDTO = createTestValidUserUpdateDtoForAdmin();

        logger.info("имею юзера в базе {}", userRepository.findByUsername("admin"));
        ObjectMapper objectMapper = createObjectMapper();
        String jsonRequest = objectMapper.writeValueAsString(updateUserDTO);

        mockMvc.perform(put("/api/users/{id}", userIdToUpdate)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username", is("newUsername")))
                .andExpect(jsonPath("$.name", is("newName")))
                .andExpect(jsonPath("$.email", is("newemail@example.com")))
                .andExpect(jsonPath("$.roles[*].name", hasItems("ROLE_ADMIN", "ROLE_USER")));
    }


//    @Test
//    void updateUserWithNotFullField() throws Exception {
//        User user1 = new User("username4", "name4", "email4@email.com", "1234");
//        userRepository.save(user1);
//
//        Long userIdToUpdate = userRepository.findByUsername("username4").get().getIdUser();
//
//        UpdateUserDTOForUser updateUserDTO = createTestValidUserUpdateDtoWithoutSomeField();
//
//        ObjectMapper objectMapper = createObjectMapper();
//        String jsonRequest = objectMapper.writeValueAsString(updateUserDTO);
//
//        mockMvc.perform(patch("/api/users/{id}", userIdToUpdate)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(jsonRequest)
//                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.username", is("newUsername")));
//    }

    @Test
    void destroy() throws Exception {
        Long userId = findCurrentUserId();
        logger.info("ID текущего пользователя = {}", userId);

        mockMvc.perform(delete("/api/users/{id}", userId)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isNoContent());

        assertThat(userRepository.findById(userId)).isEmpty();
    }


    private void createTestDocuments() {
        TypeDocument typeDocument1 = new TypeDocument();
        typeDocument1.setId(1L);
        typeDocument1.setType(NOTE);
        logger.info("создал тестовый тип для документа {}", typeDocument1);

        Document document1 = new Document();
        document1.setId(1L);
        document1.setTitle("title");
        document1.setNumber(1234L);
        document1.setContent("content");
        document1.setType(typeDocument1);
        document1.setAuthor(userRepository.findByUsername("admin").get());
        logger.info("полностью создал тестовый докумен, его айди{}", document1.getId());
        logger.info("полностью создал тестовый докумен, айди его автора {}", document1.getAuthor().getIdUser());
        documentRepository.save(document1);
    }
    private UpdateUserDTOForUser createTestNotValidUserUpdateDto() {
        UpdateUserDTOForUser updateUserDTO = new UpdateUserDTOForUser();
        updateUserDTO.setUsername(JsonNullable.of("")); // Невалидное имя пользователя
        updateUserDTO.setEmail(JsonNullable.of("invalid-email")); // Невалидный email
        return updateUserDTO;
    }
    private UpdateUserDTOForUser createTestValidUserUpdateDto() {
        UpdateUserDTOForUser updateUserDTO = new UpdateUserDTOForUser();
        updateUserDTO.setUsername(JsonNullable.of("newUsername"));
        updateUserDTO.setName(JsonNullable.of("newName"));
        updateUserDTO.setEmail(JsonNullable.of("newemail@example.com"));
        updateUserDTO.setPassword(JsonNullable.of("newPassword"));
        return updateUserDTO;
    }
    private UpdateUserDTOForUser createTestValidUserUpdateDtoWithoutSomeField() {
        UpdateUserDTOForUser updateUserDTO = new UpdateUserDTOForUser();
        updateUserDTO.setUsername(JsonNullable.of("newUsername"));
        return updateUserDTO;
    }
    private UpdateUserDTOForAdmin createTestValidUserUpdateDtoForAdmin() {
        UpdateUserDTOForAdmin updateUserDTO = new UpdateUserDTOForAdmin();
        updateUserDTO.setUsername(JsonNullable.of("newUsername"));
        updateUserDTO.setName(JsonNullable.of("newName"));
        updateUserDTO.setEmail(JsonNullable.of("newemail@example.com"));
        updateUserDTO.setPassword(JsonNullable.of("newPassword"));
        updateUserDTO.setRoleIds(JsonNullable.of(Set.of(1L))); //добавление роли админа
        return updateUserDTO;
    }
    private ObjectMapper createObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule()); // Поддержка Java Time API
        objectMapper.registerModule(new JsonNullableModule()); // Поддержка JsonNullable
        return objectMapper;
    }
    private Long findCurrentUserId () {
        return userRepository.findByUsername("admin").get().getIdUser();
    }
}