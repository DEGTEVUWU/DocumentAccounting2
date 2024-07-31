package com.ivan_degtev.documentaccounting2.mapper.config;

import com.ivan_degtev.documentaccounting2.mapper.DocumentMapper;
import com.ivan_degtev.documentaccounting2.mapper.FileEntityMapper;
import com.ivan_degtev.documentaccounting2.mapper.UserMapper;
import com.ivan_degtev.documentaccounting2.mapper.utils.impl.MappingIdAndEntityDataImpl;
import org.mapstruct.factory.Mappers;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Класс с конфигурационными бинами, которые инжектят в абстрактные классы мапперов дополнительное реализованное поле -
 * это поле с сервисным классом для маппинга Сета сущностей в Сет с id этих сущностей и обратно
 * Внедрение зависимостей в мапперы происзодит через сеттер.
 * Для расширения и использования в других маппера - добавьте новые бины
 */
@Configuration
public class MapperConfig {

    @Bean
    public DocumentMapper documentMapper(MappingIdAndEntityDataImpl mappingIdAndEntityData) {
        DocumentMapper mapper = Mappers.getMapper(DocumentMapper.class);
        mapper.setMappingIdAndEntityData(mappingIdAndEntityData);

        return mapper;
    }

    @Bean
    public FileEntityMapper fileEntityMapper(MappingIdAndEntityDataImpl mappingIdAndEntityData) {
        FileEntityMapper mapper = Mappers.getMapper(FileEntityMapper.class);
        mapper.setMappingIdAndEntityData(mappingIdAndEntityData);

        return mapper;
    }

    @Bean
    public UserMapper userMapper(MappingIdAndEntityDataImpl mappingIdAndEntityData) {
        UserMapper mapper = Mappers.getMapper(UserMapper.class);
        mapper.setMappingIdAndEntityData(mappingIdAndEntityData);

        return mapper;
    }
}