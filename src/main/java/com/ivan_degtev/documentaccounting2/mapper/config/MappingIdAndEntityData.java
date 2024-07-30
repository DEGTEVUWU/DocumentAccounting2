package com.ivan_degtev.documentaccounting2.mapper.config;

import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public interface MappingIdAndEntityData {

    <T> Set<T> convertIdsToEntities(Set<Long> ids, Class<T> entityClass);
    <T> Set<Long> convertEntitiesToIds(Set<T> entities);
}
