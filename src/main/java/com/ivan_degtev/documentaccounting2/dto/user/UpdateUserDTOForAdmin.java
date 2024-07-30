package com.ivan_degtev.documentaccounting2.dto.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.openapitools.jackson.nullable.JsonNullable;

import java.util.Set;

@Getter
@Setter
@ToString
public class UpdateUserDTOForAdmin {
    private JsonNullable<String> username;
    private JsonNullable<String> email;
    private JsonNullable<String> password;
    private JsonNullable<String> name;
    private JsonNullable<String> lastName;
    private JsonNullable<String> photo;

    @JsonProperty("role_ids")
    private JsonNullable<Set<Long>> roleIds;
}
