package com.ivan_degtev.documentaccounting2.dto.user;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UpdateUserDTOForUser extends BaseUpdateUserDTO {
}
