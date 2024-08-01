package com.ivan_degtev.documentaccounting2.service.impl;

import com.ivan_degtev.documentaccounting2.dto.document.CreateDocumentDTO;
import com.ivan_degtev.documentaccounting2.dto.document.DocumentDTO;
import com.ivan_degtev.documentaccounting2.dto.document.DocumentParamsDTO;
import com.ivan_degtev.documentaccounting2.dto.document.UpdateDocumentDTO;
import com.ivan_degtev.documentaccounting2.exceptions.NotFoundException;
import com.ivan_degtev.documentaccounting2.exceptions.ResourceNotValidException;
import com.ivan_degtev.documentaccounting2.mapper.DocumentMapper;
import com.ivan_degtev.documentaccounting2.model.Document;
import com.ivan_degtev.documentaccounting2.model.TypeDocument;
import com.ivan_degtev.documentaccounting2.model.User;
import com.ivan_degtev.documentaccounting2.repository.DocumentRepository;
import com.ivan_degtev.documentaccounting2.repository.TypeDocumentRepository;
import com.ivan_degtev.documentaccounting2.repository.UserRepository;
import com.ivan_degtev.documentaccounting2.service.DocumentService;
import com.ivan_degtev.documentaccounting2.specification.DocumentSpecification;
import com.ivan_degtev.documentaccounting2.utils.UserUtils;

import org.openapitools.jackson.nullable.JsonNullable;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class DocumentServiceImpl implements DocumentService {
    private final DocumentRepository documentRepository;
    private final TypeDocumentRepository typeDocumentRepository;
    private final DocumentMapper documentMapper;
    private final UserRepository userRepository;
    private final DocumentSpecification documentSpecification;
    private final UserUtils userUtils;

    @Transactional(readOnly = true)
    public List<DocumentDTO> getAll() {
        List<Document> documents = documentRepository.findAll();
        return documents.stream()
                .map(documentMapper::toDTO)
                .toList();
    }

    /**
     * Метод для вывода только тех документов, которые подходят по параметрам доступа(определено в контроллере)
     */
    @Transactional(readOnly = true)
    public List<DocumentDTO> getAllForUsers(Long userId) {
        List<Document> documents = documentRepository.findAllByAuthorIdUserAndPublicDocument(userId);
        return documents.stream()
                .map(documentMapper::toDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public Page<DocumentDTO> getAllByParameters(DocumentParamsDTO params, int pageNumber) {
        Specification<Document> spec = documentSpecification.build(params);
        Sort sort = documentSpecification.createSort(params);
        Pageable pageable = PageRequest.of(pageNumber - 1, 10, sort);
        Page<Document> documents = documentRepository.findAll(spec, pageable);
        return documents.map(documentMapper::toDTO);
    }

    /**
     * метод для создания док обычным юзером, проверяется через утилитарный метод, есть ли док-т с таким полем title(уникальное)
     * уже в БД
     */
    @Transactional
    public DocumentDTO create(CreateDocumentDTO documentData) throws ResourceNotValidException {
        if (documentData.getPublicDocument() == null) {
            documentData.setPublicDocument(false);
        }
        Long idCurrentUser = userUtils.getCurrentUser().getIdUser();
        documentData.setAuthorId(idCurrentUser);
        Document document = documentMapper.toDocumentFromSimpleUser(documentData);

        if (isDocumentNotValidForCreate(document)) {
            throw new ResourceNotValidException("Document with title " + document.getTitle()
                    + " already exists, creation is impossible!");
        }
        documentRepository.save(document);
        return documentMapper.toDTO(document);
    }

    @Transactional(readOnly = true)
    public DocumentDTO findById(Long id) throws ResourceNotValidException {
        var document = documentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotValidException("Document with this id " + id + " not found!"));
        return documentMapper.toDTO(document);
    }

    @Transactional
    public DocumentDTO updateForUser(UpdateDocumentDTO documentData, Long id) throws ResourceNotValidException {
        Long currentUserId = userUtils.getCurrentUser().getIdUser();
        documentData.setAuthorId(JsonNullable.of(currentUserId));
        Document document = documentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotValidException("Document with this id " + id + " not found!"));
        documentMapper.update(documentData, document);
        documentRepository.save(document);
        return documentMapper.toDTO(document);
    }
    @Transactional
    public DocumentDTO updateForAdmin(UpdateDocumentDTO documentData, Long id) throws ResourceNotValidException {
        Document document = documentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotValidException("Document with this id " + id + " not found!"));
        documentMapper.update(documentData, document);
        documentRepository.save(document);
        return documentMapper.toDTO(document);
    }

    @Transactional
    public DocumentDTO updateDocumentWithNotFullField(UpdateDocumentDTO updateDTO, Long id) {
        Document document = documentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Document not found: " + id));
        if (updateDTO.getAuthorId().isPresent()) {
            Long userId = (long) updateDTO.getAuthorId().get();
            User author = userRepository.findById(userId)
                    .orElseThrow(() -> new NotFoundException("User with id + "  + userId + " not found"));
            document.setAuthor(author);
        }
        if (updateDTO.getTitle().isPresent()) {
            updateDTO.getTitle().ifPresent(document::setTitle);
        }
        if (updateDTO.getTypeId().isPresent()) {
            Long typeId = (long) updateDTO.getTypeId().get();
            TypeDocument typeDocument = typeDocumentRepository.findById(typeId)
                    .orElseThrow(() -> new NotFoundException("TypeDocument with id + "  + typeId + " not found"));
            document.setType(typeDocument);
        }
        if (updateDTO.getContent().isPresent()) {
            updateDTO.getContent().ifPresent(document::setContent);
        }
        if (updateDTO.getNumber().isPresent()) {
            updateDTO.getNumber().ifPresent(document::setNumber);
        }
        documentRepository.save(document);
        return documentMapper.toDTO(document);
    }

    @Transactional
    public void delete(Long id) {
        documentRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    protected boolean isDocumentNotValidForCreate(Document currentDocument) {
        return documentRepository.existsDocumentByTitle(currentDocument.getTitle());
    }
}
