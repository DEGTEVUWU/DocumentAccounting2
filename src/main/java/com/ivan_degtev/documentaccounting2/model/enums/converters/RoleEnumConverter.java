package com.ivan_degtev.documentaccounting2.model.enums.converters;


import com.ivan_degtev.documentaccounting2.model.enums.RoleEnum;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class RoleEnumConverter implements AttributeConverter<RoleEnum, String> {

    @Override
    public String convertToDatabaseColumn(RoleEnum attribute) {
        return attribute != null ? attribute.name() : null;
    }

    @Override
    public RoleEnum convertToEntityAttribute(String dbData) {
        return dbData != null ? RoleEnum.valueOf(dbData) : null;
    }
}
/*
требуется для конвертации енамов из Бд в java-код и обратно
 */
