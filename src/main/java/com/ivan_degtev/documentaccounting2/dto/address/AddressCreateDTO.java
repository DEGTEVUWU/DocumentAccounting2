package com.ivan_degtev.documentaccounting2.dto.address;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class AddressCreateDTO {
    @JsonProperty("entered_full_address")
    private String enteredFullAddress;
}
