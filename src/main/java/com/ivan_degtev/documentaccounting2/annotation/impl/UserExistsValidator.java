package com.ivan_degtev.documentaccounting2.annotation.impl;

import com.ivan_degtev.documentaccounting2.annotation.ValidUser;
import com.ivan_degtev.documentaccounting2.repository.UserRepository;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;

public class UserExistsValidator implements ConstraintValidator<ValidUser, Long> {

    @Autowired
    private UserRepository userRepository;

    @Override
    public boolean isValid(Long userId, ConstraintValidatorContext context) {
        if (userId == null) {
            return true;
        }
        return userRepository.existsById(userId);
    }
}
