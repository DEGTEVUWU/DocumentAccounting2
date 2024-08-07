package com.ivan_degtev.documentaccounting2.mapper;

import com.ivan_degtev.documentaccounting2.dto.document.CreateDocumentDTO;
import com.ivan_degtev.documentaccounting2.dto.document.DocumentDTO;
import com.ivan_degtev.documentaccounting2.dto.document.UpdateDocumentDTO;
import com.ivan_degtev.documentaccounting2.mapper.config.ReferenceMapper;
import com.ivan_degtev.documentaccounting2.mapper.config.JsonNullableMapper;
import com.ivan_degtev.documentaccounting2.model.AddressEntity;
import com.ivan_degtev.documentaccounting2.model.User;
import com.ivan_degtev.documentaccounting2.mapper.utils.impl.MappingIdAndEntityDataImpl;
import com.kuliginstepan.dadata.client.domain.address.Address;
import lombok.Setter;

import org.mapstruct.*;


@Mapper(
        uses = { ReferenceMapper.class, JsonNullableMapper.class },
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public abstract class AddressMapper {
    @Mapping(source = "country", target = "country")
    @Mapping(source = "city", target = "city")
    @Mapping(source = "street", target = "street")
    @Mapping(source = "house", target = "house")
    @Mapping(source = "postalCode", target = "postalCode")
    @Mapping(source = "geoLat", target = "geoLat")
    @Mapping(source = "geoLon", target = "geoLon")
    public abstract AddressEntity mapToAddressEntity(Address address);
}
