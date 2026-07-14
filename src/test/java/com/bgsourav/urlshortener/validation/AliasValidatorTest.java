package com.bgsourav.urlshortener.validation;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class AliasValidatorTest {

    private final AliasValidator aliasValidator = new AliasValidator();

    @Test
    void acceptsValidAliases() {
        assertThat(aliasValidator.isValid("valid_alias-1", null)).isTrue();
        assertThat(aliasValidator.isValid(null, null)).isTrue();
    }

    @Test
    void rejectsInvalidLengthsAndCharacters() {
        assertThat(aliasValidator.isValid("tiny", null)).isFalse();
        assertThat(aliasValidator.isValid("a".repeat(33), null)).isFalse();
        assertThat(aliasValidator.isValid("invalid alias", null)).isFalse();
    }

    @Test
    void rejectsReservedAliasesCaseInsensitively() {
        assertThat(aliasValidator.isValid("Health", null)).isFalse();
        assertThat(aliasValidator.isValid("stats", null)).isFalse();
        assertThat(aliasValidator.isValid("shorten", null)).isFalse();
    }
}
