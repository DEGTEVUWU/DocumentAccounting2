package com.ivan_degtev.documentaccounting2.controller;

import com.ivan_degtev.documentaccounting2.controller.utils.DependenciesForTests;
import com.ivan_degtev.documentaccounting2.dto.document.CreateDocumentDTO;
import com.ivan_degtev.documentaccounting2.dto.document.DocumentParamsDTO;
import com.ivan_degtev.documentaccounting2.dto.document.UpdateDocumentDTO;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.ivan_degtev.documentaccounting2.model.Document;
import com.ivan_degtev.documentaccounting2.model.TypeDocument;
import com.ivan_degtev.documentaccounting2.repository.DocumentRepository;
import com.ivan_degtev.documentaccounting2.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.openapitools.jackson.nullable.JsonNullable;
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
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private DocumentRepository documentRepository;
    @Autowired
    private AuthController authController;
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
        createTestDocumentsForAdminAuthentication();
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

    /**
     * Используется регистрация пользователя с ролью Юзера и вход под ним
     */
    @Test
    void indexForUsers() throws Exception {
        authController.logoutUser();
        dependenciesForTests.registerAsUser();
        dependenciesForTests.loginAsUser();

        mockMvc.perform(get("/api/documents/for_user")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].title", is("title 0")))
                .andExpect(jsonPath("$[0].number", is(0)))
                .andExpect(jsonPath("$[0].content", is("content 0")))
                .andExpect(jsonPath("$[0].public_document", is(true)));
    }

    /**
     * Создается DTO с параметрами для поиска и внедряется в тест через формате json
     */
    @Test
    void searchDocumentsForAdmin() throws Exception {
        DocumentParamsDTO params = createDocumentParamsDTO();
        ObjectMapper objectMapper = dependenciesForTests.createObjectMapper();
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

    /**
     * Создается DTO с параметрами для поиска и внедряется в тест через формате json,
     * но теперь от лица сущности с ролью Юзера
     */
    @Test
    void searchDocumentsForUser() throws Exception {
        DocumentParamsDTO params = createDocumentParamsDTO();
        ObjectMapper objectMapper = dependenciesForTests.createObjectMapper();
        authController.logoutUser();
        dependenciesForTests.registerAsUser();
        dependenciesForTests.loginAsUser();

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
        CreateDocumentDTO createDocumentDTO = createTestCreateDocumentDTO();
        ObjectMapper objectMapper = dependenciesForTests.createObjectMapper();
        String jsonRequest = objectMapper.writeValueAsString(createDocumentDTO);
        Integer idCurrentUser = Math.toIntExact(getUserIdWhoHasAccess());

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
        dependenciesForTests.registerAsUser();
        dependenciesForTests.loginAsUser();

        mockMvc.perform(get("/api/documents/{documentId}", idTestDocument))
                .andExpect(status().isForbidden());
    }

    /**
     * В тесте проверяется изменения документа для авторизованного юзера,
     * проверяются все изменённые поля, кроме автора, к которому юзер не имеет доступа.
     * Изменения происходят из под аутентификации под ролью Юзер(как и первичное создание тестового документа)
     */
    @Test
    void updateForUser() throws Exception {
        authController.logoutUser();
        dependenciesForTests.registerAsUser();
        dependenciesForTests.loginAsUser();
        createTestDocumentFromUserAuthentication();

        UpdateDocumentDTO updateDocumentDTO = createTestUpdateDocumentDTO();
        ObjectMapper objectMapper = dependenciesForTests.createObjectMapper();
        String jsonRequest = objectMapper.writeValueAsString(updateDocumentDTO);

        Long idDocument = documentRepository.findAll().get(2).getId();

        /*
          поле необходимо для проверки изменения поля availableFor в сущности документа,
          по логике, оно меняется на id текущего юзера, чтоб избежать путаницы
         */
        Integer idCurrentUser = Math.toIntExact(getUserIdWhoHasAccess());

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

    /**
     * Проверить добавление для документа нового автора, используется под аутентификации с ролью Админа
     */
    @Test
    void updateForAdmin() throws Exception {
        UpdateDocumentDTO updateDocumentDTO = createTestUpdateDocumentDTO();
        updateDocumentDTO.setAuthorId(JsonNullable.of(getUserIdWhoHasAccess()));

        ObjectMapper objectMapper = dependenciesForTests.createObjectMapper();
        String jsonRequest = objectMapper.writeValueAsString(updateDocumentDTO);
        Long idDocument = documentRepository.findAll().get(0).getId();
        Integer idCurrentUser = Math.toIntExact(getUserIdWhoHasAccess());

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
        ObjectMapper objectMapper = dependenciesForTests.createObjectMapper();
        String jsonRequest = objectMapper.writeValueAsString(invalidUpdateDocumentDTO);
        Long idDocument = documentRepository.findAll().get(0).getId();

        mockMvc.perform(put("/api/documents/{id}", idDocument)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Bad Request"))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.detail")
                        .value("must not be null, must not be null, must not be null, must not be null"))
                .andExpect(jsonPath("$.instance").value("/api/documents/" + idDocument));
    }
    @Test
    void updateFromNotValidUser() throws Exception {
        authController.logoutUser();
        dependenciesForTests.registerAsUser();
        dependenciesForTests.loginAsUser();
        UpdateDocumentDTO invalidUpdateDocumentDTO = createTestUpdateDocumentDTO();
        ObjectMapper objectMapper = dependenciesForTests.createObjectMapper();
        String jsonRequest = objectMapper.writeValueAsString(invalidUpdateDocumentDTO);
        Long idDocument = documentRepository.findAll().get(0).getId();

        mockMvc.perform(put("/api/documents/{id}", idDocument)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403))
                .andExpect(jsonPath("$.detail").value("Access Denied"))
                .andExpect(jsonPath("$.instance").value(""));
    }

    @Test
    void destroy() throws Exception {
        Long documentId = documentRepository.findAll().get(0).getId();

        mockMvc.perform(delete("/api/documents/{id}", documentId)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isNoContent());

        assertThat(documentRepository.findById(documentId)).isEmpty();
    }

    @Test
    void destroyFromNotValidUser() throws Exception {
        authController.logoutUser();
        dependenciesForTests.registerAsUser();
        dependenciesForTests.loginAsUser();
        Long documentId = documentRepository.findAll().get(0).getId();

        mockMvc.perform(delete("/api/documents/{id}", documentId)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isForbidden());
    }


    /*
     * Ниже идут технические методы для тестов с кратким описанием
     */


     /**
     * тех метод, для создания и сохранения в Базу тестовых документов из под авторизации юзера с ролью Юзера
     */
    private void createTestDocumentFromUserAuthentication() {
        TypeDocument typeDocument = new TypeDocument();
        typeDocument.setId((long) 100);
        typeDocument.setType(NOTE);

        Document document = new Document();
        document.setId((long) 100);
        document.setTitle("title 100");
        document.setNumber((long) 100);
        document.setContent("content 100");
        document.setType(typeDocument);
        document.setAuthor(userRepository.findByUsername("user").get());
        document.setPublicDocument(true);

        documentRepository.save(document);
    }

    /**
     * тех метод, для создания и сохранения в Базу тестовых документов из под авторизации юзера с ролью Админа
     * (создаются 2 документа)
     */
    private void createTestDocumentsForAdminAuthentication() {
        for (var i = 0; i < 2; i++) {
            TypeDocument typeDocument1 = new TypeDocument();
            typeDocument1.setId((long) i);
            typeDocument1.setType(NOTE);

            Document document1 = new Document();
            document1.setId((long) i);
            document1.setTitle("title " + i);
            document1.setNumber((long) i);
            document1.setContent("content " + i);
            document1.setType(typeDocument1);
            document1.setAuthor(userRepository.findByUsername("admin").get());
            document1.setPublicDocument(i == 0);

            documentRepository.save(document1);
        }
    }

    /**
     * тех метод, для создания dto с параметрами поиска документов
     */
    private DocumentParamsDTO createDocumentParamsDTO() {
        DocumentParamsDTO params = new DocumentParamsDTO();
        params.setTitleCont("title");
        params.setAuthorCont("admin");
        params.setSortBy("title");
        params.setSortDirection("asc");
        return params;
    }

    /**
     * тех метод, для создания dto для создания тестового документа
     */
    private CreateDocumentDTO createTestCreateDocumentDTO() {
        CreateDocumentDTO createDocumentDTO = new CreateDocumentDTO();
        createDocumentDTO.setNumber(1L);
        createDocumentDTO.setTitle("title");
        createDocumentDTO.setContent("content");
        createDocumentDTO.setAuthorId(getUserIdWhoHasAccess());
        createDocumentDTO.setTypeId(5L);
        createDocumentDTO.setPublicDocument(true);
        createDocumentDTO.setAvailableFor(Set.of(getUserIdWhoHasAccess()));
        return createDocumentDTO;
    }

    /**
     * тех метод, для создания dto для изменения тестового документа
     */
    private UpdateDocumentDTO createTestUpdateDocumentDTO() {
        UpdateDocumentDTO updateDocumentDTO = new UpdateDocumentDTO();
        updateDocumentDTO.setTitle(JsonNullable.of("newTitle"));
        updateDocumentDTO.setContent(JsonNullable.of("newContent"));
        updateDocumentDTO.setNumber(JsonNullable.of((long) 1000));
        updateDocumentDTO.setTypeId(JsonNullable.of((long) 5));
        updateDocumentDTO.setPublicDocument(false);
        updateDocumentDTO.setAvailableFor(JsonNullable.of(Set.of(getUserIdWhoHasAccess())));
        return updateDocumentDTO;
    }

    /**
     * тех метод, для создания dto для изменения тестового документа с невалидными полями null
     */
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

    /**
     * Технический метод, который всегда возращает id админа, используется в создании разных документов и дто,
     * для внедрения в поле AvailableFor - чтоб протестировать передачу кому-то доступа на просмотр документа
     */
    private Long getUserIdWhoHasAccess() {
        return userRepository.findByUsername("admin").get().getIdUser();
    }
}