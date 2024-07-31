package com.ivan_degtev.documentaccounting2.mapper;

import com.ivan_degtev.documentaccounting2.dto.document.CreateDocumentDTO;
import com.ivan_degtev.documentaccounting2.dto.document.DocumentDTO;
import com.ivan_degtev.documentaccounting2.dto.document.UpdateDocumentDTO;
import com.ivan_degtev.documentaccounting2.mapper.config.ReferenceMapper;
import com.ivan_degtev.documentaccounting2.mapper.config.JsonNullableMapper;
import com.ivan_degtev.documentaccounting2.model.User;
import com.ivan_degtev.documentaccounting2.mapper.utils.impl.MappingIdAndEntityDataImpl;
import lombok.Setter;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

import com.ivan_degtev.documentaccounting2.model.Document;

import java.util.Set;

@Mapper(
        uses = { ReferenceMapper.class, JsonNullableMapper.class },
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
@Setter
public abstract class DocumentMapper {

    protected MappingIdAndEntityDataImpl mappingIdAndEntityData;

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

    /**
     * @param users - сет юзеров
     * @return сет id этих юзеров
     * Используется для маппинга Сета сущностей юзера в id этих юзеров
     */
    @Named("mappingFromEntityToDto")
    public Set<Long> mappingFromEntityToDto(Set<User> users) {
        return mappingIdAndEntityData.convertEntitiesToIds(users);
    }

    /**
     * @param userIds - сет id
     * @return сет сущностей юзера
     * Используется для маппинга Сета id юзеров в Сет самих сущностей юзера
     */
    @Named("mappingFromDtoToEntity")
    public Set<User> mappingFromDtoToEntity(Set<Long> userIds) {
        return mappingIdAndEntityData.convertIdsToEntities(userIds, User.class);
    }
}
