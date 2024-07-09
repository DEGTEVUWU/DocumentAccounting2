package com.ivan_degtev.documentaccounting2.mapper;

import com.ivan_degtev.documentaccounting2.dto.document.CreateDocumentDTO;
import com.ivan_degtev.documentaccounting2.dto.document.DocumentDTO;
import com.ivan_degtev.documentaccounting2.dto.document.UpdateDocumentDTO;
import com.ivan_degtev.documentaccounting2.utils.UserUtils;
import lombok.AllArgsConstructor;
import org.mapstruct.*;
import com.ivan_degtev.documentaccounting2.model.Document;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(
        uses = { ReferenceMapper.class, JsonNullableMapper.class },
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public abstract class DocumentMapper {
    @Mapping(source = "authorId", target = "author")
    @Mapping(source = "typeId", target = "type")
    public abstract Document toDocumentFromSimpleUser(CreateDocumentDTO dto);

    @Mapping(source = "author.idUser", target = "author")
    @Mapping(source = "type.id", target = "type")
    public abstract DocumentDTO toDTO(Document document);

    @Mapping(source = "authorId", target = "author")
    @Mapping(source = "typeId", target = "type")
    public abstract void update(UpdateDocumentDTO dto, @MappingTarget Document document);

//    @Named("savingCurrentUserId")
//    public Long savingCurrentUserId(Long someId) {
//        return userUtils.getCurrentUser().getIdUser();
//    }
}
