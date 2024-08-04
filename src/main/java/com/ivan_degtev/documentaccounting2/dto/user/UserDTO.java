package com.ivan_degtev.documentaccounting2.dto.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.ivan_degtev.documentaccounting2.dto.address.AddressDTO;
import com.ivan_degtev.documentaccounting2.model.Role;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import java.util.Set;

@Getter
@Setter
@ToString
public class UserDTO {
    private Long id;
    private String username;
    private String email;
    private String password;
    private String name;
    @JsonProperty("last_name")
    private String lastName;
    @JsonIgnore
    private String photo;
    private Set<Role> roles;
    private String address;
}
