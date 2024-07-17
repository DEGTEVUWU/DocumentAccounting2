package com.ivan_degtev.documentaccounting2.component;

import com.ivan_degtev.documentaccounting2.exceptions.NotFoundException;
import com.ivan_degtev.documentaccounting2.model.Document;
import com.ivan_degtev.documentaccounting2.model.Role;
import com.ivan_degtev.documentaccounting2.model.TypeDocument;
import com.ivan_degtev.documentaccounting2.model.User;
import com.ivan_degtev.documentaccounting2.model.enums.RoleEnum;
import com.ivan_degtev.documentaccounting2.repository.DocumentRepository;
import com.ivan_degtev.documentaccounting2.repository.TypeDocumentRepository;
import com.ivan_degtev.documentaccounting2.repository.UserRepository;
import com.ivan_degtev.documentaccounting2.service.RoleService;
import lombok.AllArgsConstructor;
import net.datafaker.Faker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.ZoneId;
import java.util.*;

@Component
@AllArgsConstructor
public class DataInitializer implements ApplicationRunner {
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final RoleService roleService;
    private final DocumentRepository documentRepository;
    private final TypeDocumentRepository typeDocumentRepository;
    private static final Faker faker = new Faker();
    private final Logger logger = LoggerFactory.getLogger(DataInitializer.class);

    @Override
    public void run(ApplicationArguments args) {
        createAdminEntity();
        createModeratorEntity();
        logger.info("создал админа и модера");
//        createDocuments();
    }

    public void createTestDocuments() {
        createDocuments();
    }
    private void createAdminEntity() {
        Optional<User> firstUser = userRepository.findByEmail("diogteff.ivan@yandex.com");
        if (firstUser.isEmpty()) {
            var adminData = new User();

            String email = "diogteff.ivan@yandex.com";
            String username = "admin";
            String name = "Ivan";
            adminData.setName(name);
            adminData.setUsername(username);
            adminData.setEmail(email);
            adminData.setPassword(passwordEncoder.encode("password"));
            adminData.setRoles(Set.of(createRoleAdmin(), createRoleUser()));
            logger.info("создал админа {}", adminData.toString());
            userRepository.save(adminData);
            logger.info("закинул в репо админа {}", adminData.toString());
        }
    }


    private void createModeratorEntity() {
        Optional<User> firstUser = userRepository.findByEmail("example@yandex.com");
        if (firstUser.isEmpty()) {
            var moderData = new User();

            String email = "example@yandex.com";
            String username = "moder";
            String name = "Moder";
            moderData.setName(name);
            moderData.setUsername(username);
            moderData.setEmail(email);
            moderData.setPassword(passwordEncoder.encode("password"));
            moderData.setRoles(Set.of(createRoleModerator()));
            logger.info("создал модера {}", moderData.toString());
            userRepository.save(moderData);
            logger.info("закинул в репо модера {}", moderData.toString());

        }
    }

    private Role createRoleUser() {
        return roleService.findByName(RoleEnum.ROLE_USER);
    }
    private Role createRoleAdmin() {
        return roleService.findByName(RoleEnum.ROLE_ADMIN);
    }
    private Role createRoleModerator() {
        return roleService.findByName(RoleEnum.ROLE_MODERATOR);
    }
    private  void createDocuments() {
        for (int i = 0; i < 1; i++) {
            Document document = new Document();
            document.setId((long) i + 1);
            document.setTitle(faker.book().title());
            document.setAuthor(getAdmin());
            document.setContent(faker.lorem().paragraph());
            document.setType(getDefaultType());
            document.setCreationDate(faker.date().birthday().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
            document.setNumber(faker.number().randomNumber());
            document.setAvailableFor(Set.of());

            logger.info("сделал объект документа с полями юзер {}", document.getAuthor().getUsername());
            logger.info("сделал объект документа с полями тип {}", document.getType().getType());
            try {
                documentRepository.save(document);
                logger.info("Document saved successfully: {}", document);
            } catch (Exception e) {
                logger.error("Error saving document: {}", document, e);
            }
        }
    }
    private User getAdmin() {
        return userRepository.findByUsername("admin")
                .orElseThrow(() -> new NotFoundException("User with username admin not found!"));
    }
    private TypeDocument getDefaultType() {
        return typeDocumentRepository.findById(5L)
                .orElseThrow(() -> new NotFoundException("Type document with id 5 not found!"));
    }
}
