package com.ivan_degtev.documentaccounting2.model.enums.converters;

import com.ivan_degtev.documentaccounting2.model.enums.TypeDocumentEnum;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class TypeDocumentEnumConverter implements AttributeConverter<TypeDocumentEnum, String> {

    @Override
    public String convertToDatabaseColumn(TypeDocumentEnum attribute) {
        return attribute != null ? attribute.name() : null;
    }

    @Override
    public TypeDocumentEnum convertToEntityAttribute(String dbData) {
        return dbData != null ? TypeDocumentEnum.valueOf(dbData) : null;
    }
}
/*
требуется для конвертации енамов из Бд в java-код и обратно
 */