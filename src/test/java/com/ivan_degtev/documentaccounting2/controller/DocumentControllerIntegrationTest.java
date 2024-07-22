package com.ivan_degtev.documentaccounting2.controller;

import com.ivan_degtev.documentaccounting2.dto.auth.UserRegisterDTO;
import com.ivan_degtev.documentaccounting2.dto.document.CreateDocumentDTO;
import com.ivan_degtev.documentaccounting2.dto.document.DocumentParamsDTO;
import com.ivan_degtev.documentaccounting2.dto.document.UpdateDocumentDTO;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.ivan_degtev.documentaccounting2.component.DataInitializer;
import com.ivan_degtev.documentaccounting2.dto.auth.LoginRequestDTO;
import com.ivan_degtev.documentaccounting2.model.Document;
import com.ivan_degtev.documentaccounting2.model.TypeDocument;
import com.ivan_degtev.documentaccounting2.repository.DocumentRepository;
import com.ivan_degtev.documentaccounting2.repository.UserRepository;
import com.ivan_degtev.documentaccounting2.utils.UserUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.openapitools.jackson.nullable.JsonNullable;
import org.openapitools.jackson.nullable.JsonNullableModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;


import java.util.Map;
import java.util.Set;


import static com.ivan_degtev.documentaccounting2.model.enums.TypeDocumentEnum.NOTE;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Slf4j
class DocumentControllerIntegrationTest {

    private final Logger logger = LoggerFactory.getLogger(DocumentControllerIntegrationTest.class);
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
    void create() throws Exception {
        CreateDocumentDTO createDocumentDTO = createTestDocumentDTO();
        ObjectMapper objectMapper = createObjectMapper();
        String jsonRequest = objectMapper.writeValueAsString(createDocumentDTO);
        Integer idCurrentUser = Math.toIntExact(getIdCurrentUser());

        mockMvc.perform(post("/api/documents")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title", is("title")))
                .andExpect(jsonPath("$.number", is(1)))
                .andExpect(jsonPath("$.author.username", is("admin")))
                .andExpect(jsonPath("$.content", is("content")))
                .andExpect(jsonPath("$.type.type", is("DEFAULT_DOCUMENT")))
                .andExpect(jsonPath("$.public_document", is(true)))
                .andExpect(jsonPath("$.available_for[0]", is(idCurrentUser)));
    }

    @Test
    void show() throws Exception {
        Long idTestDocument = documentRepository.findAll().get(0).getId();
        ObjectMapper objectMapper = createObjectMapper();

        mockMvc.perform(get("/api/documents/{documentId}", idTestDocument))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title", is("title 0")))
                .andExpect(jsonPath("$.number", is(0)))
                .andExpect(jsonPath("$.author.username", is("admin")))
                .andExpect(jsonPath("$.content", is("content 0")))
                .andExpect(jsonPath("$.type.type", is("NOTE")))
                .andExpect(jsonPath("$.public_document", is(true)));
    }
    @Test
    void showNotFoundDocument() throws Exception {
        Long idTestDocument = documentRepository.findAll().get(0).getId();
        mockMvc.perform(get("/api/documents/{documentId}", idTestDocument + 10))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Document with this id " + (idTestDocument + 10) + " not found!"));
    }
    @Test
    void showForUser() throws Exception {
        Long idTestDocument = documentRepository.findAll().get(1).getId();
        authController.logoutUser();
        registerNeUser();
        loginNeUser();

        mockMvc.perform(get("/api/documents/{documentId}", idTestDocument))
                .andExpect(status().isForbidden());
    }

    @Test
    void updateForUser() throws Exception {
//        authController.logoutUser();
//        registerNeUser();
//        String userToken = loginNeUser();
        // зайти под юзером
        UpdateDocumentDTO updateDocumentDTO = createTestUpdateDocumentDTO();
        ObjectMapper objectMapper = createObjectMapper();
        String jsonRequest = objectMapper.writeValueAsString(updateDocumentDTO);
        Long idDocument = documentRepository.findAll().get(0).getId();
        Integer idCurrentUser = Math.toIntExact(getIdCurrentUser());

        mockMvc.perform(put("/api/documents/{idDocument}", idDocument)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title", is("newTitle")))
                .andExpect(jsonPath("$.number", is(1000)))
                .andExpect(jsonPath("$.content", is("newContent")))
                .andExpect(jsonPath("$.type.type", is("DEFAULT_DOCUMENT")))
                .andExpect(jsonPath("$.public_document", is(false)))
                .andExpect(jsonPath("$.available_for[0]", is(idCurrentUser)));
    }

    @Test
    void updateForAdmin() throws Exception {
        UpdateDocumentDTO updateDocumentDTO = createTestUpdateDocumentDTO();
        updateDocumentDTO.setAuthorId(JsonNullable.of(getIdCurrentUser()));
        // добавить в dto нового автора для документа
        ObjectMapper objectMapper = createObjectMapper();
        String jsonRequest = objectMapper.writeValueAsString(updateDocumentDTO);
        Long idDocument = documentRepository.findAll().get(0).getId();
        Integer idCurrentUser = Math.toIntExact(getIdCurrentUser());

        mockMvc.perform(put("/api/documents/for_admin/{idDocument}", idDocument)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title", is("newTitle")))
                .andExpect(jsonPath("$.number", is(1000)))
                .andExpect(jsonPath("$.content", is("newContent")))
                .andExpect(jsonPath("$.type.type", is("DEFAULT_DOCUMENT")))
                .andExpect(jsonPath("$.author.idUser", is(idCurrentUser)))
                .andExpect(jsonPath("$.public_document", is(false)))
                .andExpect(jsonPath("$.available_for[0]", is(idCurrentUser)));
    }
    @Test
    void updateWithInvalidData() throws Exception {
        UpdateDocumentDTO invalidUpdateDocumentDTO = createTestNotValidUpdateDocumentDTO();
        ObjectMapper objectMapper = createObjectMapper();
        String jsonRequest = objectMapper.writeValueAsString(invalidUpdateDocumentDTO);
        Long idDocument = documentRepository.findAll().get(0).getId();

        mockMvc.perform(put("/api/documents/{id}", idDocument)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Bad Request"))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.detail").value("must not be null, must not be null, must not be null, must not be null"))
                .andExpect(jsonPath("$.instance").value("/api/documents/" + idDocument));
    }
    @Test
    void updateFromNotValidUser() throws Exception {
        authController.logoutUser();
        registerNeUser();
        loginNeUser();
        UpdateDocumentDTO invalidUpdateDocumentDTO = createTestUpdateDocumentDTO();
        ObjectMapper objectMapper = createObjectMapper();
        String jsonRequest = objectMapper.writeValueAsString(invalidUpdateDocumentDTO);
        Long idDocument = documentRepository.findAll().get(0).getId();

        mockMvc.perform(put("/api/documents/{id}", idDocument)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isForbidden())
//                .andExpect(jsonPath("$.title").value("Bad Request"))
                .andExpect(jsonPath("$.status").value(403))
                .andExpect(jsonPath("$.detail").value("Access Denied"))
                .andExpect(jsonPath("$.instance").value(""));
    }


//    @Test
//    void updateDocumentWithNotFullField() throws Exception {
//        UpdateDocumentDTO updateDocumentDTO = createTestUpdateDocumentDTOWithNotFullFields();
//
//        ObjectMapper objectMapper = createObjectMapper();
//        String jsonRequest = objectMapper.writeValueAsString(updateDocumentDTO);
//        Long idDocument = documentRepository.findAll().get(0).getId();
//        Integer idCurrentUser = Math.toIntExact(getIdCurrentUser());
//
//        mockMvc.perform(patch("/api/documents/{idDocument}", idDocument)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(jsonRequest)
//                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.title", is("newTitleNotFields")))
//                .andExpect(jsonPath("$.number", is(1000)))
//                .andExpect(jsonPath("$.content", is("newContentNotFields")))
//                .andExpect(jsonPath("$.type.type", is("DEFAULT_DOCUMENT")))
//                .andExpect(jsonPath("$.author.idUser", is(idCurrentUser)))
//                .andExpect(jsonPath("$.public_document", is(true)))
//                .andExpect(jsonPath("$.available_for[0]", is(null)));
//    }

    @Test
    void destroy() throws Exception {
        Long documentId = documentRepository.findAll().get(0).getId();
        logger.info("ID документа = {}", documentId);

        mockMvc.perform(delete("/api/documents/{id}", documentId)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isNoContent());

        assertThat(documentRepository.findById(documentId)).isEmpty();
    }
    @Test
    void destroyFromNotValidUser() throws Exception {
        authController.logoutUser();
        registerNeUser();
        loginNeUser();
        Long documentId = documentRepository.findAll().get(0).getId();
        logger.info("ID документа = {}", documentId);

        mockMvc.perform(delete("/api/documents/{id}", documentId)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isForbidden());

//        assertThat(documentRepository.findById(documentId)).isEmpty();
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
            log.info("сделал документ тестовый {}", document1.toString());
            document1.setPublicDocument(i == 0);
            log.info("добавил в тестовый документ модификатор доступа {}", document1.toString());

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
    private String loginNeUser() {
        LoginRequestDTO loginRequestDTO = new LoginRequestDTO("user", "1234");
        var response = authController.authenticateUser(loginRequestDTO);
        logger.info("авторизовался под тестовым юзером {}", userRepository.findByUsername("user").get().getIdUser());
        String userToken = ((Map<String, String>) response.getBody()).get("jwtToken");
        logger.info("получил токен и установил его в глобальную переменную {}", token);
        return userToken;

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
    private CreateDocumentDTO createTestDocumentDTO() {
        CreateDocumentDTO createDocumentDTO = new CreateDocumentDTO();
        createDocumentDTO.setNumber(1L);
        createDocumentDTO.setTitle("title");
        createDocumentDTO.setContent("content");
        createDocumentDTO.setAuthorId(getIdCurrentUser());
        createDocumentDTO.setTypeId(5L);
        createDocumentDTO.setPublicDocument(true);
        createDocumentDTO.setAvailableFor(Set.of(getIdCurrentUser()));
        return createDocumentDTO;
    }
    private UpdateDocumentDTO createTestUpdateDocumentDTO() {
        UpdateDocumentDTO updateDocumentDTO = new UpdateDocumentDTO();
        updateDocumentDTO.setTitle(JsonNullable.of("newTitle"));
        updateDocumentDTO.setContent(JsonNullable.of("newContent"));
        updateDocumentDTO.setNumber(JsonNullable.of((long) 1000));
        updateDocumentDTO.setTypeId(JsonNullable.of((long) 5));
        updateDocumentDTO.setPublicDocument(false);
        updateDocumentDTO.setAvailableFor(JsonNullable.of(Set.of(getIdCurrentUser())));
        return updateDocumentDTO;
    }
//    private UpdateDocumentDTO createTestUpdateDocumentDTOWithNotFullFields() {
//        UpdateDocumentDTO updateDocumentDTO = new UpdateDocumentDTO();
//        updateDocumentDTO.setTitle(JsonNullable.of("newTitleNotFields"));
//        updateDocumentDTO.setContent(JsonNullable.of("newContentNotFields"));
//        updateDocumentDTO.setAuthorId(JsonNullable.of(getIdCurrentUser()));
//        updateDocumentDTO.setAvailableFor(JsonNullable.of(Set.of(getIdCurrentUser())));
//        return updateDocumentDTO;
//    }
    private UpdateDocumentDTO createTestNotValidUpdateDocumentDTO() {
        UpdateDocumentDTO invalidUpdateDocumentDTO = new UpdateDocumentDTO();
        invalidUpdateDocumentDTO.setTitle(JsonNullable.of(null));
        invalidUpdateDocumentDTO.setNumber(JsonNullable.of(null));
        invalidUpdateDocumentDTO.setAuthorId(JsonNullable.of(null));
        invalidUpdateDocumentDTO.setContent(JsonNullable.of(null));
        invalidUpdateDocumentDTO.setTypeId(JsonNullable.of(null));
        invalidUpdateDocumentDTO.setPublicDocument(false);
        invalidUpdateDocumentDTO.setAvailableFor(JsonNullable.of(null));
        return invalidUpdateDocumentDTO;
    }
    private Long getIdCurrentUser() {
        return userRepository.findByUsername("admin").get().getIdUser();
    }
}