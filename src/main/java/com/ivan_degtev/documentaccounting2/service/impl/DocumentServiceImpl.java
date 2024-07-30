package com.ivan_degtev.documentaccounting2.service.impl;

import com.ivan_degtev.documentaccounting2.controller.AuthController;
import com.ivan_degtev.documentaccounting2.dto.document.CreateDocumentDTO;
import com.ivan_degtev.documentaccounting2.dto.document.DocumentDTO;
import com.ivan_degtev.documentaccounting2.dto.document.DocumentParamsDTO;
import com.ivan_degtev.documentaccounting2.dto.document.UpdateDocumentDTO;
import com.ivan_degtev.documentaccounting2.exceptions.NotFoundException;
import com.ivan_degtev.documentaccounting2.exceptions.ResourceNotValidException;
import com.ivan_degtev.documentaccounting2.mapper.config.DocumentMapper;
import com.ivan_degtev.documentaccounting2.mapper.config.JsonNullableMapper;
import com.ivan_degtev.documentaccounting2.mapper.UserMapper;
import com.ivan_degtev.documentaccounting2.model.Document;
import com.ivan_degtev.documentaccounting2.model.TypeDocument;
import com.ivan_degtev.documentaccounting2.model.User;
import com.ivan_degtev.documentaccounting2.repository.DocumentRepository;
import com.ivan_degtev.documentaccounting2.repository.TypeDocumentRepository;
import com.ivan_degtev.documentaccounting2.repository.UserRepository;
import com.ivan_degtev.documentaccounting2.service.DocumentService;
import com.ivan_degtev.documentaccounting2.service.UserService;
import com.ivan_degtev.documentaccounting2.specification.DocumentSpecification;
import com.ivan_degtev.documentaccounting2.utils.UserUtils;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.openapitools.jackson.nullable.JsonNullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class DocumentServiceImpl implements DocumentService {

    private final Logger logger = LoggerFactory.getLogger(DocumentServiceImpl.class);
    private final DocumentRepository documentRepository;
    private final TypeDocumentRepository typeDocumentRepository;
    private final DocumentMapper documentMapper;
    private final JsonNullableMapper jsonNullableMapper;
    private final UserRepository userRepository;
    private final UserService userService;
    private final AuthController authController;
    private final DocumentSpecification documentSpecification;
    private final UserMapper userMapper;
    private final UserUtils userUtils;

    public List<DocumentDTO> getAll() {
        List<Document> documents = documentRepository.findAll();
        logger.info("получил лист всех документов {}", documents);
        List<DocumentDTO> resultList = documents.stream()
                .map(documentMapper::toDTO)
                .toList();
        logger.info("замапил их в  лист всех дто {}", resultList);
        return resultList;
    }
    public List<DocumentDTO> getAllForUsers(Long userId) {
        List<Document> documents = documentRepository.findAllByAuthorIdUserAndPublicDocument(userId);
        List<DocumentDTO> resultList = documents.stream()
                .map(documentMapper::toDTO)
                .toList();
        return resultList;
    }

    public Page<DocumentDTO> getAllByParameters(DocumentParamsDTO params, int pageNumber) {
        Specification<Document> spec = documentSpecification.build(params);
        logger.info("имею спецификацию из параметров {}", spec.toString());
        Sort sort = documentSpecification.createSort(params);
        logger.info("имею   сортировку из параметров {}", sort.toString());
        Pageable pageable = PageRequest.of(pageNumber - 1, 10, sort);
        logger.info("имею пейджебел из параметров {}", pageable.toString());
        Page<Document> documents = documentRepository.findAll(spec, pageable);
        logger.info("имею страницу документов из репозитория  {}", documents.toString());
        return documents.map(documentMapper::toDTO);
    }

    /*
    метод для создания док обычным юзером
     */
    @Transactional
    public DocumentDTO create(CreateDocumentDTO documentData) throws ResourceNotValidException {
        logger.info(" зашёл в сервис создания док-та {}", documentData);
        if (documentData.getPublicDocument() == null) {
            documentData.setPublicDocument(false); // Установите значение по умолчанию, если оно не указано
        }
        Long idCurrentUser = userUtils.getCurrentUser().getIdUser();
        logger.info("айди текущего юзера {}", idCurrentUser);
        documentData.setAuthorId(idCurrentUser);
        logger.info("добавил в него айди текущего {}", documentData.toString());
        Document document = documentMapper.toDocumentFromSimpleUser(documentData);
        logger.info("замапил из дто в сам документ = {}", document.toString());

        if (isDocumentNotValidForCreate(document)) {
            throw new ResourceNotValidException("Document with title " + document.getTitle()
                    + " already exists, creation is impossible!");
        }
        documentRepository.save(document);
        logger.info("сохранил документ в репо");
        DocumentDTO documentDTO = documentMapper.toDTO(document);
        logger.info("замапил из дока в дто снова = {}", documentDTO.toString());
        return documentDTO;
    }

    @Transactional
    public DocumentDTO findById(Long id) throws ResourceNotValidException {
        var document = documentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotValidException("Document with this id " + id + " not found!"));
        DocumentDTO documentDTO = documentMapper.toDTO(document);
        return documentDTO;
    }

    @Transactional
    public DocumentDTO updateForUser(UpdateDocumentDTO documentData, Long id) throws ResourceNotValidException {
        Long currentUserId = userUtils.getCurrentUser().getIdUser();
        documentData.setAuthorId(JsonNullable.of(currentUserId));
        logger.info("получили такое дто {}", documentData.toString());
        Document document = documentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotValidException("Document with this id " + id + " not found!"));
        logger.info("нашли док по айди");
        documentMapper.update(documentData, document);
        logger.info("замапили в сущность {}", document.toString());
        documentRepository.save(document);
        logger.info("сохранил сущность в репо");
        DocumentDTO documentDTO = documentMapper.toDTO(document);
        logger.info("замапил сущность в дто");
        return documentDTO;
    }
    @Transactional
    public DocumentDTO updateForAdmin(UpdateDocumentDTO documentData, Long id) throws ResourceNotValidException {
        logger.info("получили такое дто {}", documentData.toString());
        Document document = documentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotValidException("Document with this id " + id + " not found!"));
        logger.info("нашли док по айди");
        documentMapper.update(documentData, document);
        logger.info("замапили в сущность {}", document.toString());
        documentRepository.save(document);
        logger.info("сохранил сущность в репо");
        DocumentDTO documentDTO = documentMapper.toDTO(document);
        logger.info("замапил сущность в дто");
        return documentDTO;
    }

    @Transactional
    public DocumentDTO updateDocumentWithNotFullField(UpdateDocumentDTO updateDTO, Long id) {
        logger.info("зашел в метод с дто неполной для патча + {}", updateDTO);
        Document document = documentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Document not found: " + id));
        logger.info("нашёл документ + {}", document);
        if (updateDTO.getAuthorId().isPresent()) {
            logger.info("зашел в иф автора + {}", updateDTO);
            Long userId = (long) updateDTO.getAuthorId().get();
            User author = userRepository.findById(userId)
                    .orElseThrow(() -> new NotFoundException("User with id + "  + userId + " not found"));
            logger.info("нашёл автора + {}", author);
            document.setAuthor(author);
            logger.info("эзасетил в документ автора");
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

    public void delete(Long id) {
        documentRepository.deleteById(id);
    }

    private boolean isDocumentNotValidForCreate(Document currentDocument) {
        return documentRepository.existsDocumentByTitle(currentDocument.getTitle());
    }
}
