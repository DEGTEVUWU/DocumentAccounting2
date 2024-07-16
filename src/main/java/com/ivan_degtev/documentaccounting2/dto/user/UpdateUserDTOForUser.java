package com.ivan_degtev.documentaccounting2.dto.user;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.openapitools.jackson.nullable.JsonNullable;

@Getter
@Setter
@ToString
public class UpdateUserDTOForUser {
    private JsonNullable<String> username;
    private JsonNullable<String> email;
    private JsonNullable<String> password;
    private JsonNullable<String> name;
    private JsonNullable<String> lastName;
    private JsonNullable<String> photo;
}
