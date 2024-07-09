package com.ivan_degtev.documentaccounting2.service;

import com.ivan_degtev.documentaccounting2.dto.document.CreateDocumentDTO;
import com.ivan_degtev.documentaccounting2.dto.document.DocumentDTO;
import com.ivan_degtev.documentaccounting2.dto.document.DocumentParamsDTO;
import com.ivan_degtev.documentaccounting2.dto.document.UpdateDocumentDTO;
import com.ivan_degtev.documentaccounting2.model.Document;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface DocumentService {
    List<DocumentDTO> getAll();
    List<DocumentDTO> getAllForUsers(Long userId);
    Page<DocumentDTO> getAllByParameters(DocumentParamsDTO documentParamsDTO, int pageNumber);
    DocumentDTO create(CreateDocumentDTO documentDTO);
    DocumentDTO findById(Long id);
    DocumentDTO updateForUser(UpdateDocumentDTO documentDTO, Long id);
    DocumentDTO updateForAdmin(UpdateDocumentDTO documentDTO, Long id);
    DocumentDTO updateDocumentWithNotFullField(UpdateDocumentDTO documentDTO, Long id);
    void delete(Long id);

}
