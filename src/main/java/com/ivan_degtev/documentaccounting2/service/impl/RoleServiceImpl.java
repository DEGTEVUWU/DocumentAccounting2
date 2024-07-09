package com.ivan_degtev.documentaccounting2.service.impl;

import com.ivan_degtev.documentaccounting2.exceptions.NotFoundException;
import com.ivan_degtev.documentaccounting2.model.Role;
import com.ivan_degtev.documentaccounting2.model.enums.RoleEnum;
import com.ivan_degtev.documentaccounting2.repository.RoleRepository;
import com.ivan_degtev.documentaccounting2.service.RoleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RoleServiceImpl implements RoleService {

    Logger logger = LoggerFactory.getLogger(RoleServiceImpl.class);

    @Autowired
    RoleRepository roleRepository;

    @Override
    public Role findByName(RoleEnum role) {
        logger.info("зашёл в метод найти по имени, передал роль-енам");
        return roleRepository.findByName(role).orElseThrow(() -> new NotFoundException("Role not found"));
    }
}
