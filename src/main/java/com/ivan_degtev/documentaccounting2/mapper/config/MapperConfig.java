package com.ivan_degtev.documentaccounting2.mapper.config;

import com.ivan_degtev.documentaccounting2.service.other.UserMappingImpl;
import org.mapstruct.factory.Mappers;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MapperConfig {

    @Bean
    public DocumentMapper documentMapper(UserMappingImpl userMapping) {
        DocumentMapper mapper = Mappers.getMapper(DocumentMapper.class);
        mapper.setUserMapping(userMapping);

        return mapper;
    }
}