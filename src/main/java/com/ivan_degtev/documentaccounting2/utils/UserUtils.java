package com.ivan_degtev.documentaccounting2.utils;

import com.ivan_degtev.documentaccounting2.exceptions.NotAuthenticatedException;
import com.ivan_degtev.documentaccounting2.exceptions.NotFoundException;
import com.ivan_degtev.documentaccounting2.exceptions.ResourceNotValidException;
import com.ivan_degtev.documentaccounting2.model.Document;
import com.ivan_degtev.documentaccounting2.model.FileEntity;
import com.ivan_degtev.documentaccounting2.model.User;
import com.ivan_degtev.documentaccounting2.model.interfaces.Authorable;
import com.ivan_degtev.documentaccounting2.model.interfaces.Available;
import com.ivan_degtev.documentaccounting2.repository.DocumentRepository;
import com.ivan_degtev.documentaccounting2.repository.FileRepository;
import com.ivan_degtev.documentaccounting2.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
@Slf4j
public class UserUtils {
    private final UserRepository userRepository;
    private final DocumentRepository documentRepository;
    private final FileRepository fileRepository;

    /**
     * Утилитный метод получения данных текущего юзера из секьюрити контекста
     */
    public User getCurrentUser() throws NotAuthenticatedException {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        log.info("получил аутентификацию = {}", authentication);
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }
        var username = authentication.getName();
        User currentUser = userRepository.findByUsername(username).get();
        log.info("текущий юзер это {}", currentUser.toString());
        return currentUser;
    }


    /**
     *     метод определяет является ли текущий юзер аутентифицированным
     */
    public boolean currentUserIsAuthorForFiles(Long fileId) {
        FileEntity fileEntity = fileRepository.findById(fileId)
                .orElseThrow(() -> new ResourceNotValidException("File with this id " + fileId + " not found!"));
        return currentUserIsAuthor(fileEntity);
    }

    /**
     *     метод определяет является ли текущий юзер автором текущего документа( id док-та должно быть передано)
     */
    public boolean currentUserIsAuthorForDocuments(Long documentId) {
        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new ResourceNotValidException("Document with this id " + documentId + " not found!"));
        return currentUserIsAuthor(document);
    }

    /**
     *     метод определяет является ли текущий юзер автором текущего сущности-наследника интерфейса Authorable
     *     ( id сущности-наследника должно быть передано)
     *     Существует для последующего расширения
     */
    private boolean currentUserIsAuthor(Authorable entity) {
        User currentUser = getCurrentUser();
        return entity.getAuthor().getIdUser().equals(currentUser.getIdUser());
    }

    /**
     *     метод определяет является ли текущий документ(id должно быть переданно) доступным по флагу публичности
     *     или доступным для текущего юзера
     */
    public boolean currentDocumentIsPublicOrAvailableForDocuments(Long documentId) {
        User currentUser = getCurrentUser();
        Document currentDocument = documentRepository.findById(documentId)
                .orElseThrow(() -> new NotFoundException("Document with this id " + documentId + " not found!"));
        log.info("зашел в проверку доступности документа, текущего юзера и документа нашёл  {}", currentDocument);

        return currentDocument.getPublicDocument() ||
                currentDocument.getAvailableFor()
                .stream()
                .anyMatch(user -> user.getIdUser().equals(currentUser.getIdUser()));
    }

    /**
     *     метод определяет является ли текущий документ(id должно быть переданно) доступным по флагу публичности
     *     или доступным для текущего юзера
     */
    public boolean currentFileEntityIsPublicOrAvailableForFileEntity(Long fileEntityId) {
        FileEntity currentFileEntity = fileRepository.findById(fileEntityId)
                .orElseThrow(() -> new NotFoundException("FileEntity with this id " + fileEntityId + " not found!"));
        return currentEntityIsPublicOrAvailable(currentFileEntity);
    }

    /**
     *     Утилитный метод определяет является ли текущий наследний интерфейска Available(id должно быть переданно)
     *     доступным по флагу публичности или доступным для текущего юзера.
     *     Используется для последующего расширения функционала.
     *     Помещается общая логика определеня доступности, на которую делают вызов из личных методов для каждой сущности,
     *     по типу currentFileEntityIsPublicOrAvailableForFileEntity
     */
    private boolean currentEntityIsPublicOrAvailable(Available entity) {
        User currentUser = getCurrentUser();
        log.info("зашел в общий метод проверки доступности, текущий юзер:  {}", currentUser);
        return entity.getPublicEntity() ||
                entity.getAvailableFor()
                        .stream()
                        .anyMatch(user -> user.getIdUser().equals(currentUser.getIdUser()));

    }
}