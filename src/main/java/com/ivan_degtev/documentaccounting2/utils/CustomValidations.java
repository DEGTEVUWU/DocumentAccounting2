package com.ivan_degtev.documentaccounting2.utils;

import com.ivan_degtev.documentaccounting2.dto.document.UpdateDocumentDTO;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class CustomValidations {

//    @Bean
//    public UpdateDocumentDTO validateUpdateDocumentDTOWithNullFields(UpdateDocumentDTO documentDTO) {
//        UpdateDocumentDTO updateDocumentDTOWithoutNullFields = new UpdateDocumentDTO();
//        if (documentDTO.getTypeId() != null) {
//            updateDocumentDTOWithoutNullFields.setTypeId(documentDTO.getTypeId());
//        }
//        if (documentDTO.getTitle() != null) {
//            updateDocumentDTOWithoutNullFields.setTitle(documentDTO.getTitle());
//        }
//        if (documentDTO.getNumber() != null) {
//            updateDocumentDTOWithoutNullFields.setNumber(documentDTO.getNumber());
//        }
//        if (documentDTO.getAuthorId() != null) {
//            updateDocumentDTOWithoutNullFields.setAuthorId(documentDTO.getAuthorId());
//        }
//        if (documentDTO.getContent() != null) {
//            updateDocumentDTOWithoutNullFields.setContent(documentDTO.getContent());
//        }
//        return updateDocumentDTOWithoutNullFields;
//    }
}
