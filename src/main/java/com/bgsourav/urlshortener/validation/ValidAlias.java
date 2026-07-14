package com.bgsourav.urlshortener.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD, ElementType.PARAMETER, ElementType.RECORD_COMPONENT})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = AliasValidator.class)
public @interface ValidAlias {

    String message() default "alias must be 5-32 characters using letters, digits, underscores, or hyphens";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
