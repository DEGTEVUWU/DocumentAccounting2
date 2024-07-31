package com.ivan_degtev.documentaccounting2.mapper.utils.impl;

import com.ivan_degtev.documentaccounting2.mapper.utils.MappingIdAndEntityData;
import com.ivan_degtev.documentaccounting2.model.Role;
import com.ivan_degtev.documentaccounting2.model.User;
import com.ivan_degtev.documentaccounting2.repository.RoleRepository;
import com.ivan_degtev.documentaccounting2.repository.UserRepository;
import io.swagger.annotations.Scope;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
@Scope(name = "prototype", description = "")
public class MappingIdAndEntityDataImpl implements MappingIdAndEntityData {
    private final RoleRepository roleRepository;
    private UserRepository userRepository;

    /**
     * Метод, реализует интерфейс и служит для маппинга полей сущностей, которые представлены в виде Сетов id других сущностей,
     * происходит маппинг в Сеты самих сущностей.
     * Расширяемый(добавьте другие условия с использованием другого класса искомой сущнсти)
     */
    @Override
    public <T> Set<T> convertIdsToEntities(Set<Long> ids, Class<T> entityClass) {
        if (entityClass.equals(User.class)) {
            return (Set<T>) new HashSet<>(userRepository.findAllById(ids));
        }
        if (entityClass.equals(Role.class)) {
            return (Set<T>) new HashSet<>(roleRepository.findAllById(ids));
        }
        throw new IllegalArgumentException("Unsupported entity class: " + entityClass.getName());
    }

    /**
     * Метод, реализует интерфейс и служит для обратного маппинга - поле с Сетом любых сущностей
     * будет замапено в Сет с id этих сущностей
     */
    @Override
    public <T> Set<Long> convertEntitiesToIds(Set<T> entities) {
        return entities.stream()
                .map(entity -> {
                    if (entity instanceof User) {
                        return ((User) entity).getIdUser();
                    }
                    throw new IllegalArgumentException("Unsupported entity class: " + entity.getClass().getName());
                })
                .collect(Collectors.toSet());
    }
}
