package com.ivan_degtev.documentaccounting2.dto.fileEntity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.openapitools.jackson.nullable.JsonNullable;

import java.util.Set;

@Setter
@Getter
@ToString
public class FileEntityUpdateDTO {
    @JsonProperty(value = "public_document", defaultValue = "false")
    private Boolean publicEntity = false;
    @JsonProperty("available_for")
    private Set<Long> availableFor;
    @JsonProperty("entered_address")
    private JsonNullable<String> enteredAddress = JsonNullable.undefined();
//    private String enteredAddress;

}

