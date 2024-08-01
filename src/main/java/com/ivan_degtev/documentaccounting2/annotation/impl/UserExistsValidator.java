package com.ivan_degtev.documentaccounting2.annotation.impl;

import com.ivan_degtev.documentaccounting2.annotation.ValidUser;
import com.ivan_degtev.documentaccounting2.repository.UserRepository;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;

public class UserExistsValidator implements ConstraintValidator<ValidUser, Long> {

    @Autowired
    private UserRepository userRepository;

    /**
     * Идея аннотации в том, чтобы проверять существует ли юзер с указанным id в БД при создании документа
     * с использованием поля authorId.
     * Необходима потому, что по умолчанию, id автора подставляется в сервисном методе по созданию док-та и берётся
     * из утилитарного метода по SecurityContextHolder - то есть - это всегда id текущего юзера
     * Аннотация помогает в  возможных случаях, если создается документ, используя все поля из CreateDocumentDTO
     * (это возможно в будущем от роли Админа, при расширении функционала приложения)
     */
    @Override
    public boolean isValid(Long userId, ConstraintValidatorContext context) {
        if (userId == null) {
            return true;
        }
        return userRepository.existsById(userId);
    }
}
