package com.ivan_degtev.documentaccounting2.mapper;

import com.ivan_degtev.documentaccounting2.dto.file.FileEntityDTO;
import com.ivan_degtev.documentaccounting2.dto.fileEntity.FileEntityUpdateDTO;
import com.ivan_degtev.documentaccounting2.model.FileEntity;
import com.ivan_degtev.documentaccounting2.model.User;
import com.ivan_degtev.documentaccounting2.repository.UserRepository;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(
        uses = { ReferenceMapper.class, JsonNullableMapper.class },
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public abstract class FileEntityMapper {

    @Autowired
    private UserRepository userRepository;

    @Mapping(source = "author.username", target = "author")
    @Mapping(source = "availableFor", target = "availableFor", qualifiedByName = "mappingFromEntityToDto")
    public abstract FileEntityDTO toFileEntityDTO(FileEntity file);

    @Mapping(source = "publicEntity", target = "publicEntity")
    @Mapping(source = "availableFor", target = "availableFor", qualifiedByName = "mappingFromDtoToEntity")
    public abstract void update(
            FileEntityUpdateDTO fileEntityUpdateDTO,
            @MappingTarget FileEntity fileEntity
    );

    @Named("mappingFromEntityToDto")
    public Set<Long> mappingFromEntityToDto(Set<User> users) {
        if (users == null || users.isEmpty()) {
            return new HashSet<>();
        }
        return users.stream()
                .map(User::getIdUser)
                .collect(Collectors.toSet());
    }

    @Named("mappingFromDtoToEntity")
    public Set<User> mappingFromDtoToEntity(Set<Long> userIds) {
        if (userIds == null || userIds.isEmpty()) {
            return new HashSet<>();
        }
        return new HashSet<>(userRepository.findAllById(userIds));
    }
}
