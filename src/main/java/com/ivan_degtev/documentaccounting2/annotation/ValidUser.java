package com.ivan_degtev.documentaccounting2.annotation;

import com.ivan_degtev.documentaccounting2.annotation.impl.UserExistsValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = UserExistsValidator.class)
@Target({ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidUser {
    String message() default "Invalid user";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
