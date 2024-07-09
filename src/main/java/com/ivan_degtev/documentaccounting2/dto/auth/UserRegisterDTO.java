package com.ivan_degtev.documentaccounting2.dto.auth;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class UserRegisterDTO {
    private String name;
    private String username;
    private String email;
    private String password;
}
