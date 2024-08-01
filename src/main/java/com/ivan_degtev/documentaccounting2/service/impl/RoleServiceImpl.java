package com.ivan_degtev.documentaccounting2.service.impl;

import com.ivan_degtev.documentaccounting2.exceptions.NotFoundException;
import com.ivan_degtev.documentaccounting2.model.Role;
import com.ivan_degtev.documentaccounting2.model.enums.RoleEnum;
import com.ivan_degtev.documentaccounting2.repository.RoleRepository;
import com.ivan_degtev.documentaccounting2.service.RoleService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Slf4j
public class RoleServiceImpl implements RoleService {

    private RoleRepository roleRepository;

    @Override
    public Role findByName(RoleEnum role) {
        return roleRepository.findByName(role).orElseThrow(() -> new NotFoundException("Role not found"));
    }
}
