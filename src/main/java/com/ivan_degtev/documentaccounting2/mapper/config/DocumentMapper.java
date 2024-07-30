package com.ivan_degtev.documentaccounting2.mapper.config;

import com.ivan_degtev.documentaccounting2.dto.document.CreateDocumentDTO;
import com.ivan_degtev.documentaccounting2.dto.document.DocumentDTO;
import com.ivan_degtev.documentaccounting2.dto.document.UpdateDocumentDTO;
import com.ivan_degtev.documentaccounting2.mapper.ReferenceMapper;
import com.ivan_degtev.documentaccounting2.model.User;
import com.ivan_degtev.documentaccounting2.service.other.UserMappingImpl;
import lombok.AllArgsConstructor;
import lombok.Setter;
import org.mapstruct.*;
import com.ivan_degtev.documentaccounting2.model.Document;
import org.springframework.stereotype.Component;

import java.util.Set;

@Mapper(
        uses = { ReferenceMapper.class, JsonNullableMapper.class },
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
@Setter
public abstract class DocumentMapper {

    protected UserMappingImpl userMapping;


    @Mapping(source = "authorId", target = "author")
    @Mapping(source = "typeId", target = "type")
    @Mapping(source = "publicDocument", target = "publicDocument")
    @Mapping(source = "availableFor", target = "availableFor")
    public abstract Document toDocumentFromSimpleUser(CreateDocumentDTO dto);

    @Mapping(source = "author.idUser", target = "author")
    @Mapping(source = "type.id", target = "type")
    @Mapping(source = "publicDocument", target = "publicDocument")
    @Mapping(source = "availableFor", target = "availableFor", qualifiedByName = "mappingFromEntityToDto")
    public abstract DocumentDTO toDTO(Document document);

    @Mapping(source = "authorId", target = "author")
    @Mapping(source = "typeId", target = "type")
    @Mapping(source = "publicDocument", target = "publicDocument")
    @Mapping(source = "availableFor", target = "availableFor", qualifiedByName = "mappingFromDtoToEntity")
    public abstract void update(UpdateDocumentDTO dto, @MappingTarget Document document);

    @Named("mappingFromEntityToDto")
    public Set<Long> mappingFromEntityToDto(Set<User> users) {
        return userMapping.convertEntitiesToIds(users);
    }

    @Named("mappingFromDtoToEntity")
    public Set<User> mappingFromDtoToEntity(Set<Long> userIds) {
        return userMapping.convertIdsToEntities(userIds, User.class);
    }
}
