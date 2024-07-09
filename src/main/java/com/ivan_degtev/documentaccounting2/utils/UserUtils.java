package com.ivan_degtev.documentaccounting2.utils;

import com.ivan_degtev.documentaccounting2.exceptions.ResourceNotValidException;
import com.ivan_degtev.documentaccounting2.model.Document;
import com.ivan_degtev.documentaccounting2.model.User;
import com.ivan_degtev.documentaccounting2.repository.DocumentRepository;
import com.ivan_degtev.documentaccounting2.repository.TypeDocumentRepository;
import com.ivan_degtev.documentaccounting2.repository.UserRepository;
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
//    @Bean
    public User getCurrentUser() {
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


    public boolean currentUserIsAuthor(Long documentId) {
        User currentUser = getCurrentUser();
        if (currentUser == null) {
            return false;
        }
        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new ResourceNotValidException("Document with this id " + documentId + " not found!"));
        if (document == null) {
            return false;
        }
        return document.getAuthor().getIdUser().equals(currentUser.getIdUser());
    }
}