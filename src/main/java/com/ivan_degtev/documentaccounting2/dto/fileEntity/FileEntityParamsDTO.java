package com.ivan_degtev.documentaccounting2.dto.fileEntity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ivan_degtev.documentaccounting2.model.User;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Set;

@Setter
@Getter
@ToString
public class FileEntityParamsDTO {
    @JsonProperty(value = "public_document", defaultValue = "false")
    private Boolean publicEntity = false;

    @JsonProperty("available_for")
    private Set<Long> availableFor;
}

