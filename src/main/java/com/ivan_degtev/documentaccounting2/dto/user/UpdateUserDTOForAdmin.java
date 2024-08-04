package com.ivan_degtev.documentaccounting2.dto.user;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.openapitools.jackson.nullable.JsonNullable;

import java.util.Set;

@Getter
@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UpdateUserDTOForAdmin extends BaseUpdateUserDTO {
    @JsonProperty("role_ids")
    private JsonNullable<Set<Long>> roleIds;
}
