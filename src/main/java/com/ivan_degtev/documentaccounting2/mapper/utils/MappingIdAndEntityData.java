package com.ivan_degtev.documentaccounting2.mapper.utils;

import org.springframework.stereotype.Service;

import java.util.Set;

/**
 * Интерфейс описывает способы маппинга Сета с сущностями в Сет с id этих сущностей и обратный маппинг
 * Должен быть реализован для работы с маппером
 */
@Service
public interface MappingIdAndEntityData {
    <T> Set<T> convertIdsToEntities(Set<Long> ids, Class<T> entityClass);
    <T> Set<Long> convertEntitiesToIds(Set<T> entities);
}
