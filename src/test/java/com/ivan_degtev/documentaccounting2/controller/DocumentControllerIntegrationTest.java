package com.ivan_degtev.documentaccounting2.controller;

import com.ivan_degtev.documentaccounting2.dto.auth.UserRegisterDTO;
import com.ivan_degtev.documentaccounting2.dto.document.DocumentDTO;
import com.ivan_degtev.documentaccounting2.dto.document.DocumentParamsDTO;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.test.context.ActiveProfiles;

import com.ivan_degtev.documentaccounting2.component.DataInitializer;
import com.ivan_degtev.documentaccounting2.dto.auth.LoginRequestDTO;
import com.ivan_degtev.documentaccounting2.dto.user.UpdateUserDTOForAdmin;
import com.ivan_degtev.documentaccounting2.dto.user.UpdateUserDTOForUser;
import com.ivan_degtev.documentaccounting2.exceptions.NotFoundException;
import com.ivan_degtev.documentaccounting2.model.Document;
import com.ivan_degtev.documentaccounting2.model.Role;
import com.ivan_degtev.documentaccounting2.model.TypeDocument;
import com.ivan_degtev.documentaccounting2.model.User;
import com.ivan_degtev.documentaccounting2.model.enums.RoleEnum;
import com.ivan_degtev.documentaccounting2.repository.DocumentRepository;
import com.ivan_degtev.documentaccounting2.repository.TypeDocumentRepository;
import com.ivan_degtev.documentaccounting2.repository.UserRepository;
import com.ivan_degtev.documentaccounting2.utils.JwtUtils;
import com.ivan_degtev.documentaccounting2.utils.UserUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openapitools.jackson.nullable.JsonNullable;
import org.openapitools.jackson.nullable.JsonNullableModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;


import java.util.Map;


import static com.ivan_degtev.documentaccounting2.model.enums.RoleEnum.ROLE_MODERATOR;
import static com.ivan_degtev.documentaccounting2.model.enums.TypeDocumentEnum.NOTE;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class DocumentControllerIntegrationTest {

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
    private UserUtils userUtils;
    private String token;


    @BeforeEach
    @Transactional
    public void setUpForEach() {
        userRepository.deleteAll();
        documentRepository.deleteAll();
        authController.logoutUser();
        dataInitializer.run(null);

        logger.info("все юзеры в базе {}", userRepository.findAll().size());

        LoginRequestDTO loginRequestDTO = new LoginRequestDTO("admin", "password");
        Map<String, Object> authResponse = (Map<String, Object>) authController.authenticateUser(loginRequestDTO).getBody();
        token = (String) authResponse.get("jwtToken");
        logger.info("сделал авторизацию через админа и имею токен {}", token);

        createTestDocuments();
        logger.info("закинул 1 тестовый документ в базу {}", documentRepository.findAll().size());
    }

    @Test
    void shouldReturnListOfDocuments() throws Exception {
        mockMvc.perform(get("/api/documents")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].title", is("title 0")))
                .andExpect(jsonPath("$[1].title", is("title 1")))
                .andExpect(jsonPath("$[0].number", is(0)))
                .andExpect(jsonPath("$[1].number", is(1)))
                .andExpect(jsonPath("$[0].content", is("content 0")))
                .andExpect(jsonPath("$[1].content", is("content 1")))
                .andExpect(jsonPath("$[0].public_document", is(true)))
                .andExpect(jsonPath("$[1].public_document", is(false)));
    }

    @Test
    void indexForUsers() throws Exception {
        authController.logoutUser();
        registerNeUser();
        loginNeUser();

        mockMvc.perform(get("/api/documents/for_users")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].title", is("title 0")))
                .andExpect(jsonPath("$[0].number", is(0)))
                .andExpect(jsonPath("$[0].content", is("content 0")))
                .andExpect(jsonPath("$[0].public_document", is(true)));
    }

    @Test
    void searchDocumentsForAdmin() throws Exception {
        DocumentParamsDTO params = createDocumentParamsDTO();
        ObjectMapper objectMapper = createObjectMapper();
        String jsonParams = objectMapper.writeValueAsString(params);

        mockMvc.perform(get("/api/documents/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonParams)
                        .param("pageNumber", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].title", is("title 0")))
                .andExpect(jsonPath("$.content[0].author.username", is("admin")))
                .andExpect(jsonPath("$.content[1].title", is("title 1")))
                .andExpect(jsonPath("$.content[1].author.username", is("admin")))
                .andDo(print())
                .andReturn();
    }
    @Test
    void searchDocumentsForUser() throws Exception {
        DocumentParamsDTO params = createDocumentParamsDTO();
        ObjectMapper objectMapper = createObjectMapper();
        authController.logoutUser();
        registerNeUser();
        loginNeUser();

        String jsonParams = objectMapper.writeValueAsString(params);

        mockMvc.perform(get("/api/documents/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonParams)
                        .param("pageNumber", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].title", is("title 0")))
                .andExpect(jsonPath("$.content[0].author.username", is("admin")))
                .andDo(print())
                .andReturn();
    }

    @Test
    void create() {
    }

    @Test
    void show() {
    }

    @Test
    void updateForUser() {
    }

    @Test
    void updateForAdmin() {
    }

    @Test
    void updateDocumentWithNotFullField() {
    }

    @Test
    void delete() {
    }

    private void createTestDocuments() {


        for (var i = 0; i < 2; i++) {
            TypeDocument typeDocument1 = new TypeDocument();
            typeDocument1.setId((long) i);
            typeDocument1.setType(NOTE);
            logger.info("создал тестовый тип для документа {}", typeDocument1);

            Document document1 = new Document();
            document1.setId((long) i);
            document1.setTitle("title " + i);
            document1.setNumber((long) i);
            document1.setContent("content " + i);
            document1.setType(typeDocument1);
            document1.setAuthor(userRepository.findByUsername("admin").get());

            document1.setPublicDocument(i == 0);

            logger.info("полностью создал тестовый докумен, его айди{}", document1.getId());
            logger.info("полностью создал тестовый докумен, айди его автора {}", document1.getAuthor().getIdUser());
            documentRepository.save(document1);
        }
    }
    private void registerNeUser() {
        UserRegisterDTO userRegisterDTO = new UserRegisterDTO();
        userRegisterDTO.setEmail("test@email.com");
        userRegisterDTO.setUsername("user");
        userRegisterDTO.setName("name");
        userRegisterDTO.setPassword("1234");
        authController.registerUser(userRegisterDTO);
        logger.info("зарегистрировал тестового юзера в БД {}", userRepository.findByUsername("user").get().getIdUser());

    }
    private void loginNeUser() {
        LoginRequestDTO loginRequestDTO = new LoginRequestDTO("user", "1234");
        authController.authenticateUser(loginRequestDTO);
        logger.info("авторизовался под тестовым юзером {}", userRepository.findByUsername("user").get().getIdUser());
    }
    private ObjectMapper createObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule()); // Поддержка Java Time API
        objectMapper.registerModule(new JsonNullableModule()); // Поддержка JsonNullable
        return objectMapper;
    }
    private DocumentParamsDTO createDocumentParamsDTO() {
        DocumentParamsDTO params = new DocumentParamsDTO();
        params.setTitleCont("title");
        params.setAuthorCont("admin");
        params.setSortBy("title");
        params.setSortDirection("asc");
        return params;
    }
}