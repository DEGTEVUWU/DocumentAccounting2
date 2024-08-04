package com.ivan_degtev.documentaccounting2.dto.address;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class AddressDTO {
    @JsonProperty("full_address")
    private String fullAddress;
    @JsonProperty("geo_lat")
    private Double geoLat;
    @JsonProperty("geo_lon")
    private Double geoLon;
}
