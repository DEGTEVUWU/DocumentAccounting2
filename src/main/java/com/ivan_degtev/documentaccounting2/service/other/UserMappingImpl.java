package com.ivan_degtev.documentaccounting2.service.other;

import com.ivan_degtev.documentaccounting2.mapper.config.MappingIdAndEntityData;
import com.ivan_degtev.documentaccounting2.model.User;
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
public class UserMappingImpl implements MappingIdAndEntityData {
    private UserRepository userRepository;

    @Override
    public <T> Set<T> convertIdsToEntities(Set<Long> ids, Class<T> entityClass) {
        if (entityClass.equals(User.class)) {
            return (Set<T>) new HashSet<>(userRepository.findAllById(ids));
        }
        throw new IllegalArgumentException("Unsupported entity class: " + entityClass.getName());
    }


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
