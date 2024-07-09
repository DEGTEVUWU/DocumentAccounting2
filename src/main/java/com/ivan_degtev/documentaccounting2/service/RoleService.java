package com.ivan_degtev.documentaccounting2.service;


import com.ivan_degtev.documentaccounting2.model.Role;
import com.ivan_degtev.documentaccounting2.model.enums.RoleEnum;
import org.springframework.stereotype.Service;

@Service
public interface RoleService {
    Role findByName(RoleEnum role);
}
