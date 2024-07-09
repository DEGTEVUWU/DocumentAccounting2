package com.ivan_degtev.documentaccounting2.controller;
import com.ivan_degtev.documentaccounting2.dto.document.CreateDocumentDTO;
import com.ivan_degtev.documentaccounting2.dto.document.DocumentDTO;
import com.ivan_degtev.documentaccounting2.dto.document.DocumentParamsDTO;
import com.ivan_degtev.documentaccounting2.dto.document.UpdateDocumentDTO;
import com.ivan_degtev.documentaccounting2.service.DocumentService;
import com.ivan_degtev.documentaccounting2.service.impl.DocumentServiceImpl;
import com.ivan_degtev.documentaccounting2.utils.UserUtils;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.flogger.Flogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.List;

@RequestMapping(path = "/api/documents")
@AllArgsConstructor
@RestController
public class DocumentController {
    private final DocumentService documentService;
    private final UserUtils userUtils;
    private final Logger logger = LoggerFactory.getLogger(DocumentServiceImpl.class);

    @GetMapping(path = "")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<DocumentDTO>> index() {
        List<DocumentDTO> users = documentService.getAll();
        return ResponseEntity.ok()
                .body(users);
    }
    @GetMapping(path = "/for_users")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<DocumentDTO>> indexForUsers() {
        Long userId = userUtils.getCurrentUser().getIdUser();
        List<DocumentDTO> users = documentService.getAllForUsers(userId);
        return ResponseEntity.ok()
                .body(users);
    }
    @GetMapping(path = "/search")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Page<DocumentDTO>> search(
            @ModelAttribute DocumentParamsDTO documentsParamsDTO,
            @RequestParam(defaultValue = "1") int pageNumber
    ) {
        logger.info("зашел в контроллер на серч, имею дто из запрос {}", documentsParamsDTO.toString()
                + " и номер страницы {}", pageNumber);
        Page<DocumentDTO> documents = documentService.getAllByParameters(documentsParamsDTO, pageNumber);
        logger.info("получил из сервиса готовую страницу");
        return ResponseEntity.ok()
                .body(documents);
    }

    @PostMapping(path = "")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<DocumentDTO> create(@Valid @RequestBody CreateDocumentDTO documentData)
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        DocumentDTO document = documentService.create(documentData);
        return ResponseEntity.status(HttpStatus.CREATED).body(document);
    }

    @GetMapping(path = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MODERATOR') or @userUtils.currentUserIsAuthor(#id)")
    public ResponseEntity<DocumentDTO> show(@PathVariable Long id) {
        logger.info("Запрошен документ с ID: {}", id);
        DocumentDTO document = documentService.findById(id);
        return ResponseEntity.status(HttpStatus.OK).body(document);
    }

    @PutMapping(path = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MODERATOR') or @userUtils.currentUserIsAuthor(#id)")
    public ResponseEntity<DocumentDTO> updateForUser(@RequestBody @Valid UpdateDocumentDTO documentData, @PathVariable Long id) {
        DocumentDTO document = documentService.updateForUser(documentData, id);
        return ResponseEntity.status(HttpStatus.OK).body(document);
    }
    @PutMapping(path = "/for_admin/{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<DocumentDTO> updateForAdmin(@RequestBody @Valid UpdateDocumentDTO documentData, @PathVariable Long id) {
        DocumentDTO document = documentService.updateForAdmin(documentData, id);
        return ResponseEntity.status(HttpStatus.OK).body(document);
    }
    @PatchMapping(path = "/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MODERATOR') or @userUtils.currentUserIsAuthor(#id)")
    public ResponseEntity<DocumentDTO> updateDocumentWithNotFullField(@PathVariable Long id,
                                                         @RequestBody UpdateDocumentDTO documentUpdateDTO) {
        DocumentDTO document = documentService.updateDocumentWithNotFullField(documentUpdateDTO, id);
        return ResponseEntity.status(HttpStatus.OK).body(document);
    }

    @DeleteMapping(path = "/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MODERATOR') or @userUtils.currentUserIsAuthor(#id)")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        documentService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
