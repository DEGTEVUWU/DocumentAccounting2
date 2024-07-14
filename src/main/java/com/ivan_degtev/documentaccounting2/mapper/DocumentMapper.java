package com.ivan_degtev.documentaccounting2.mapper;

import com.ivan_degtev.documentaccounting2.dto.document.CreateDocumentDTO;
import com.ivan_degtev.documentaccounting2.dto.document.DocumentDTO;
import com.ivan_degtev.documentaccounting2.dto.document.UpdateDocumentDTO;
import com.ivan_degtev.documentaccounting2.exceptions.NotFoundException;
import com.ivan_degtev.documentaccounting2.model.User;
import com.ivan_degtev.documentaccounting2.repository.UserRepository;
import com.ivan_degtev.documentaccounting2.utils.UserUtils;
import lombok.AllArgsConstructor;
import org.mapstruct.*;
import com.ivan_degtev.documentaccounting2.model.Document;
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
public abstract class DocumentMapper {
    @Autowired
    private UserRepository userRepository;

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
