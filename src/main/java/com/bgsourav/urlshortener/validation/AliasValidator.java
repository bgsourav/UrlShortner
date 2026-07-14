package com.bgsourav.urlshortener.validation;

import java.util.Locale;
import java.util.Set;
import java.util.regex.Pattern;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class AliasValidator implements ConstraintValidator<ValidAlias, String> {

    private static final Pattern ALIAS_PATTERN = Pattern.compile("[A-Za-z0-9_-]{5,32}");
    private static final Set<String> RESERVED_ALIASES = Set.of("shorten", "health", "stats");

    @Override
    public boolean isValid(String alias, ConstraintValidatorContext context) {
        return alias == null || (ALIAS_PATTERN.matcher(alias).matches()
                && !RESERVED_ALIASES.contains(alias.toLowerCase(Locale.ROOT)));
    }
}
