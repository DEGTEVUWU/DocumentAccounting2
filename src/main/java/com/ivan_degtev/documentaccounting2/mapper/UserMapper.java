package com.ivan_degtev.documentaccounting2.mapper;

import com.ivan_degtev.documentaccounting2.dto.auth.UserRegisterDTO;
import com.ivan_degtev.documentaccounting2.dto.user.UpdateUserDTOForAdmin;
import com.ivan_degtev.documentaccounting2.dto.user.UpdateUserDTOForUser;
import com.ivan_degtev.documentaccounting2.dto.user.UserDTO;
import com.ivan_degtev.documentaccounting2.exceptions.NotFoundException;
import com.ivan_degtev.documentaccounting2.model.Role;
import com.ivan_degtev.documentaccounting2.model.User;
import com.ivan_degtev.documentaccounting2.repository.RoleRepository;
import lombok.AllArgsConstructor;
import org.elasticsearch.client.Node;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashSet;
import java.util.Set;

@Mapper(
        uses = { ReferenceMapper.class, JsonNullableMapper.class },
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public abstract class UserMapper {
    @Autowired
    private RoleRepository roleRepository;

//    protected UserMapper(RoleRepository roleRepository) {
//        this.roleRepository = roleRepository;
//    }

    @Mapping(source = "idUser",  target = "id")
    public abstract UserDTO toDTO(User user);
    public abstract User toUser(UserRegisterDTO userRegisterDTO);
    public abstract void updateForUser(UpdateUserDTOForUser updateUserDTO, @MappingTarget User user);

    @Mapping(source = "roleIds", target = "roles", qualifiedByName = "roleIdsToModel")
    public abstract void updateForAdmin(UpdateUserDTOForAdmin updateUserDTO, @MappingTarget User user);

    @Named("roleIdsToModel")
    public Set<Role> rolesToModel(Set<Long> roleIds) {
        if (roleIds == null || roleIds.isEmpty()) {
            throw new NotFoundException("RoleIds cannot be null or empty");
        }
        return new HashSet<>(roleRepository.findAllById(roleIds));
    }

}
