package com.swyp.futsal.domain.common.annotations;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;


@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = TimeFormatValidator.class)
@Documented
public @interface TimeFormat {
    String message() default "Invalid time format. Expected format: HH:mm";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
