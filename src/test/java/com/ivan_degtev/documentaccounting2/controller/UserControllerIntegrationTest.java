package com.ivan_degtev.documentaccounting2.controller;

import com.ivan_degtev.documentaccounting2.controller.utils.DependenciesForTests;
import com.ivan_degtev.documentaccounting2.dto.user.UpdateUserDTOForAdmin;
import com.ivan_degtev.documentaccounting2.dto.user.UpdateUserDTOForUser;
import com.ivan_degtev.documentaccounting2.model.Document;
import com.ivan_degtev.documentaccounting2.model.TypeDocument;
import com.ivan_degtev.documentaccounting2.repository.DocumentRepository;
import com.ivan_degtev.documentaccounting2.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openapitools.jackson.nullable.JsonNullable;
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


import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.hasItems;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;


import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Slf4j
public class UserControllerIntegrationTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private DocumentRepository documentRepository;
    @Autowired
    private DependenciesForTests dependenciesForTests;
    private String token;

    /**
     * Перед каждым тестом происходит - очистка репозитория юзеров и документов; разлогиневание;
     * принудлительный запуск компонента DataInitializer с созданием первичных юзеров; авторизация под админом для полного доступа;
     * получения строкового представления jwtToken сессии через мапу с данными о текущей аутентификации.
     * Создаются тестовые документы через приватный метод
     */
    @BeforeEach
    @Transactional
    public void setUpForEach() {
        Map<String, Object> authResponse = dependenciesForTests.initialPreparationOfTablesAndAuthentication();
        token = (String) authResponse.get("jwtToken");

        createTestDocuments();
    }

    @Test
    void index() throws Exception {
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
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
    void updateForUser() throws Exception {
        Long userIdToUpdate = findCurrentUserId();
        UpdateUserDTOForUser updateUserDTO = createTestValidUserUpdateDto();
        ObjectMapper objectMapper = dependenciesForTests.createObjectMapper();

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

    @Test
    void updateForAdmin() throws Exception {
        Long userIdToUpdate = findCurrentUserId();

        UpdateUserDTOForAdmin updateUserDTO = createTestValidUserUpdateDtoForAdmin();

        ObjectMapper objectMapper = dependenciesForTests.createObjectMapper();
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

    @Test
    void destroy() throws Exception {
        Long userId = findCurrentUserId();

        mockMvc.perform(delete("/api/users/{id}", userId)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isNoContent());

        assertThat(userRepository.findById(userId)).isEmpty();
    }


    private void createTestDocuments() {
        TypeDocument typeDocument1 = new TypeDocument();
        typeDocument1.setId(1L);
        typeDocument1.setType(NOTE);

        Document document1 = new Document();
        document1.setId(1L);
        document1.setTitle("title");
        document1.setNumber(1234L);
        document1.setContent("content");
        document1.setType(typeDocument1);
        document1.setAuthor(userRepository.findByUsername("admin").get());
        documentRepository.save(document1);
    }
    private UpdateUserDTOForUser createTestValidUserUpdateDto() {
        UpdateUserDTOForUser updateUserDTO = new UpdateUserDTOForUser();
        updateUserDTO.setUsername(JsonNullable.of("newUsername"));
        updateUserDTO.setName(JsonNullable.of("newName"));
        updateUserDTO.setEmail(JsonNullable.of("newemail@example.com"));
        updateUserDTO.setPassword(JsonNullable.of("newPassword"));
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
    private Long findCurrentUserId () {
        return userRepository.findByUsername("admin").get().getIdUser();
    }
}