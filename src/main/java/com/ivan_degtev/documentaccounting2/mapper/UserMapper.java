package com.ivan_degtev.documentaccounting2.mapper;

import com.ivan_degtev.documentaccounting2.dto.auth.UserRegisterDTO;
import com.ivan_degtev.documentaccounting2.dto.user.UpdateUserDTOForAdmin;
import com.ivan_degtev.documentaccounting2.dto.user.UpdateUserDTOForUser;
import com.ivan_degtev.documentaccounting2.dto.user.UserDTO;
import com.ivan_degtev.documentaccounting2.mapper.config.JsonNullableMapper;
import com.ivan_degtev.documentaccounting2.mapper.config.ReferenceMapper;
import com.ivan_degtev.documentaccounting2.mapper.utils.impl.MappingIdAndEntityDataImpl;
import com.ivan_degtev.documentaccounting2.model.Role;
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
public abstract class UserMapper {

    protected MappingIdAndEntityDataImpl mappingIdAndEntityData;

    @Mapping(source = "idUser",  target = "id")
    @Mapping(source = "address.fullAddress", target = "address")
//    @Mapping(source = "")
    public abstract UserDTO toDTO(User user);
    public abstract User toUser(UserRegisterDTO userRegisterDTO);
    public abstract void updateForUser(UpdateUserDTOForUser updateUserDTO, @MappingTarget User user);

    @Mapping(source = "roleIds", target = "roles", qualifiedByName = "roleIdsToModel")
    public abstract void updateForAdmin(UpdateUserDTOForAdmin updateUserDTO, @MappingTarget User user);

    /**
     * @param roleIds - сет id
     * @return сет сущностей роли
     * Используется для маппинга Сета id ролей в Сет самих сущностей роли
     */
    @Named("roleIdsToModel")
    public Set<Role> rolesToModel(Set<Long> roleIds) {
        return mappingIdAndEntityData.convertIdsToEntities(roleIds, Role.class);
    }

}
