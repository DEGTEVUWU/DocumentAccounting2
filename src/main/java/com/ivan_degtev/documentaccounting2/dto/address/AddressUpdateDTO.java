package com.ivan_degtev.documentaccounting2.dto.address;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class AddressUpdateDTO {
    @JsonProperty("entered_full_address_for_update")
    private String enteredFullAddressForUpdate;
}
