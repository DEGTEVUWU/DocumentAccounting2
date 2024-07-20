package com.ivan_degtev.documentaccounting2.utils;

import com.ivan_degtev.documentaccounting2.exceptions.NotAuthenticatedException;
import com.ivan_degtev.documentaccounting2.exceptions.NotFoundException;
import com.ivan_degtev.documentaccounting2.exceptions.ResourceNotValidException;
import com.ivan_degtev.documentaccounting2.model.Document;
import com.ivan_degtev.documentaccounting2.model.FileEntity;
import com.ivan_degtev.documentaccounting2.model.User;
import com.ivan_degtev.documentaccounting2.model.interfaces.Authorable;
import com.ivan_degtev.documentaccounting2.repository.DocumentRepository;
import com.ivan_degtev.documentaccounting2.repository.FileRepository;
import com.ivan_degtev.documentaccounting2.repository.TypeDocumentRepository;
import com.ivan_degtev.documentaccounting2.repository.UserRepository;
import com.ivan_degtev.documentaccounting2.service.AccessService;
import com.ivan_degtev.documentaccounting2.service.impl.AccessServiceImpl;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
@Slf4j
public class UserUtils {
    private final Logger logger = LoggerFactory.getLogger(UserUtils.class);
    private final UserRepository userRepository;
    private final TypeDocumentRepository typeDocumentRepository;
    private final DocumentRepository documentRepository;
    private final FileRepository fileRepository;
//    @Bean
    public User getCurrentUser() throws NotAuthenticatedException {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        logger.info("получил аутентификацию = {}", authentication);
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }
        var username = authentication.getName();
        logger.info("получил юзернейм из аутент = {}", username);
        User currentUser = userRepository.findByUsername(username).get();
        logger.info("текущий юзер это {}", currentUser.toString());
        return currentUser;
    }
    //метод опредееляет является ли текущий юзер аутентифицированным

    public boolean currentUserIsAuthorForFiles(Long fileId) {
//        User currentUser = getCurrentUser();
        FileEntity fileEntity = fileRepository.findById(fileId)
                .orElseThrow(() -> new ResourceNotValidException("File with this id " + fileId + " not found!"));
//        logger.info("есть и текущий юзер  {}", currentUser);
//        logger.info("и файл и они не пустые, айди автора у документа равно {}", fileEntity.getAuthor());
//        return fileEntity.getAuthor().getIdUser().equals(currentUser.getIdUser());
        return currentUserIsAuthor(fileEntity);
    }

    public boolean currentUserIsAuthorForDocuments(Long documentId) {
//        User currentUser = getCurrentUser();
        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new ResourceNotValidException("Document with this id " + documentId + " not found!"));
//        logger.info("есть и текущий юзер  {}", currentUser);
//        logger.info("и документ и они не пустые, айди автора у документа равно {}", document.getAuthor());
//        return document.getAuthor().getIdUser().equals(currentUser.getIdUser());
        return currentUserIsAuthor(document);
    }
    private boolean currentUserIsAuthor(Authorable entity) {
        User currentUser = getCurrentUser();
        return entity.getAuthor().getIdUser().equals(currentUser.getIdUser());
    }

    public boolean currentDocumentIsPublicOrAvailable(Long documentId) {
        User currentUser = getCurrentUser();
        Document currentDocument = documentRepository.findById(documentId)
                .orElseThrow(() -> new NotFoundException("Document with this id " + documentId + " not found!"));
        logger.info("зашел в проверку доступности документа, текущего юзера и документа нашёл  {}", currentDocument);
        if (currentDocument.getPublicDocument()) {
            return true;
        }
        return currentDocument.getAvailableFor()
                .stream()
                .anyMatch(user -> user.getIdUser().equals(currentUser.getIdUser()));
    }
}