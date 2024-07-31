package com.ivan_degtev.documentaccounting2.mapper;

import com.ivan_degtev.documentaccounting2.dto.fileEntity.FileEntityDTO;
import com.ivan_degtev.documentaccounting2.dto.fileEntity.FileEntityUpdateDTO;
import com.ivan_degtev.documentaccounting2.mapper.config.JsonNullableMapper;
import com.ivan_degtev.documentaccounting2.mapper.config.ReferenceMapper;
import com.ivan_degtev.documentaccounting2.mapper.utils.impl.MappingIdAndEntityDataImpl;
import com.ivan_degtev.documentaccounting2.model.FileEntity;
import com.ivan_degtev.documentaccounting2.model.User;
import lombok.Setter;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

import java.util.Set;

@Mapper(
        uses = { ReferenceMapper.class, JsonNullableMapper.class },
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
@Setter
public abstract class FileEntityMapper {

    protected MappingIdAndEntityDataImpl mappingIdAndEntityData;

    @Mapping(source = "author.username", target = "author")
    @Mapping(source = "availableFor", target = "availableFor", qualifiedByName = "mappingFromEntityToDto")
    public abstract FileEntityDTO toFileEntityDTO(FileEntity file);

    @Mapping(source = "publicEntity", target = "publicEntity")
    @Mapping(source = "availableFor", target = "availableFor", qualifiedByName = "mappingFromDtoToEntity")
    public abstract void update(
            FileEntityUpdateDTO fileEntityUpdateDTO,
            @MappingTarget FileEntity fileEntity
    );

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
