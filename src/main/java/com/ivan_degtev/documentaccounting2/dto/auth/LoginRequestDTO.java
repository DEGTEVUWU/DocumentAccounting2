package com.ivan_degtev.documentaccounting2.dto.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
public class LoginRequestDTO {
    @NotBlank
    private String username;
    @NotBlank
    private String password;

}