package com.ivan_degtev.documentaccounting2.controller.testcontainer;

import com.ivan_degtev.documentaccounting2.component.DataInitializer;
import com.ivan_degtev.documentaccounting2.controller.AuthController;
import com.ivan_degtev.documentaccounting2.dto.auth.LoginRequestDTO;
import com.ivan_degtev.documentaccounting2.dto.user.UpdateUserDTOForUser;
import com.ivan_degtev.documentaccounting2.model.User;
import com.ivan_degtev.documentaccounting2.repository.DocumentRepository;
import com.ivan_degtev.documentaccounting2.repository.TypeDocumentRepository;
import com.ivan_degtev.documentaccounting2.repository.UserRepository;
import com.ivan_degtev.documentaccounting2.utils.JwtUtils;
import com.ivan_degtev.documentaccounting2.utils.UserUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;


import java.util.Map;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;



@SpringBootTest
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Testcontainers
@Transactional
class UserControllerTest {
    private final Logger logger = LoggerFactory.getLogger(UserControllerTest.class);

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private DocumentRepository documentRepository;
    @Autowired
    private TypeDocumentRepository typeDocumentRepository;
    @Autowired
    private AuthController authController;
    @Autowired
    private DataInitializer dataInitializer;
    @Autowired
    private UserUtils userUtils;
    @Autowired
    private JwtUtils jwtUtils;
    private String token;

    @Container
    public static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:14-alpine")
            .withDatabaseName("integration-tests-db")
            .withUsername("testuser")
            .withPassword("testpass");

    @BeforeAll
    public static void setUp() {
        System.setProperty("DB_URL", postgreSQLContainer.getJdbcUrl());
        System.setProperty("DB_USERNAME", postgreSQLContainer.getUsername());
        System.setProperty("DB_PASSWORD", postgreSQLContainer.getPassword());
    }
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

        LoginRequestDTO loginRequestDTO = new LoginRequestDTO("admin", "password");
        Map<String, Object> authResponse = (Map<String, Object>) authController.authenticateUser(loginRequestDTO).getBody();
        token = (String) authResponse.get("jwtToken");

        logger.info("все юзеры в базе {}", userRepository.findAll());
        logger.info("сделал авторизацию через админа и имею токен {}", token);

//        logger.info("создал тестовый документ");
//        TypeDocument typeDocument1 = new TypeDocument();
//        typeDocument1.setId(1L);
//        typeDocument1.setType(NOTE);
//        typeDocumentRepository.save(typeDocument1);
//        logger.info("создал тестовый тип для документа {}", typeDocument1);
//
//        User admin = userRepository.findByUsername("admin")
//                .orElseThrow(() -> new NotFoundException("User with username admin not found!"));
//
//        Document document1 = new Document();
//        document1.setId(1L);
//        document1.setTitle("title");
//        document1.setNumber(1234L);
//        document1.setContent("content");
//        document1.setType(typeDocument1);
//        document1.setAuthor(admin);
//        document1.setAvailableFor(new HashSet<>(Collections.singletonList(admin)));
//        logger.info("полностью создал тестовый докумен, его айди{}", document1.getId());
//        logger.info("полностью создал тестовый докумен, айди его автора {}", document1.getAuthor().getIdUser());
//        documentRepository.save(document1);
//        logger.info("сохранил документ в репозиторий");


//        dataInitializer.createTestDocuments();



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
                .andExpect(jsonPath("$.id", is(3)))
                .andExpect(jsonPath("$.username", is("admin")));
    }

//    @Test
//    void checkCurrentUserIsAuthor() throws Exception {
//        mockMvc.perform(get("/api/users/check-current-user-is-author/{documentId}", 1L))
//                .andExpect(status().isOk())
//                .andExpect(content().json("true"));
//    }


    @Test
    void show() throws Exception {
        mockMvc.perform(get("/api/users/{userId}", 3L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(3)))
                .andExpect(jsonPath("$.username", is("admin")));
    }

    @Test
//    @WithMockUser(roles = {"USER", "ADMIN"})
    void updateForUser_ShouldReturnUpdatedUser_WhenValidData() throws Exception {
        // Arrange
        Long userIdToUpdate = 3L;  // Предполагается, что пользователь с id = 1 существует
        UpdateUserDTOForUser updateUserDTO = new UpdateUserDTOForUser();
        updateUserDTO.setUsername(JsonNullable.of("newUsername"));
        updateUserDTO.setName(JsonNullable.of("newName"));
        updateUserDTO.setEmail(JsonNullable.of("newemail@example.com"));
        updateUserDTO.setPassword(JsonNullable.of("newPassword"));

        logger.info("имею юзера в базе {}", userRepository.findByUsername("admin"));
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule()); // Поддержка Java Time API
        objectMapper.registerModule(new JsonNullableModule()); // Поддержка JsonNullable
        String jsonRequest = objectMapper.writeValueAsString(updateUserDTO);

        // Act
        mockMvc.perform(put("/api/users/{id}", userIdToUpdate)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userIdToUpdate)))
                .andExpect(jsonPath("$.username", is("newUsername")))
                .andExpect(jsonPath("$.name", is("newName")))
                .andExpect(jsonPath("$.email", is("newemail@example.com")));
    }

    @Test
    void updateForAdmin() throws Exception {
    }

    @Test
    void updateUserWithNotFullField() throws Exception {
    }

    @Test
    void delete() throws Exception {
    }
    private void createTestDocuments() {
//        TypeDocument typeDocument1 = new TypeDocument();
//        typeDocument1.setId(1L);
//        typeDocument1.setType(NOTE);
//        logger.info("создал тестовый тип для документа {}", typeDocument1);
//
//        Document document1 = new Document();
//        document1.setId(1L);
//        document1.setTitle("title");
//        document1.setNumber(1234L);
//        document1.setContent("content");
//        document1.setType(typeDocument1);
//        document1.setAuthor(userRepository.findByUsername("admin").get());
//        logger.info("полностью создал тестовый докумен, его айди{}", document1.getId());
//        logger.info("полностью создал тестовый докумен, айди его автора {}", document1.getAuthor().getIdUser());
//        documentRepository.save(document1);
    }
}