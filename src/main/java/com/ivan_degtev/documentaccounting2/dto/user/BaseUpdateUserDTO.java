package com.ivan_degtev.documentaccounting2.dto.user;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.openapitools.jackson.nullable.JsonNullable;

@Getter
@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public abstract class BaseUpdateUserDTO {
    private JsonNullable<String> username;
    private JsonNullable<String> email;
    private JsonNullable<String> password;
    private JsonNullable<String> name;
    @JsonProperty("last_name")
    private JsonNullable<String> lastName;
    private JsonNullable<String> photo;
    @JsonProperty("entered_address")
    private JsonNullable<String> enteredAddress = JsonNullable.undefined();
}