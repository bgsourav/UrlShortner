package com.bgsourav.urlshortener.validation;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import com.bgsourav.urlshortener.exception.InvalidUrlException;

class UrlValidatorTest {

    private final UrlValidator urlValidator = new UrlValidator();

    @ParameterizedTest
    @ValueSource(strings = {"javascript:alert(1)", "data:text/plain,hello", "file:///tmp/secret"})
    void rejectsUnsafeSchemes(String url) {
        assertThatThrownBy(() -> urlValidator.validate(url))
                .isInstanceOf(InvalidUrlException.class)
                .hasMessage("URL scheme must be http or https");
    }

    @ParameterizedTest
    @ValueSource(strings = {"https:/missing-host", "https://?query=value"})
    void rejectsUrlsWithoutAHost(String url) {
        assertThatThrownBy(() -> urlValidator.validate(url))
                .isInstanceOf(InvalidUrlException.class)
                .hasMessage("URL must include a host");
    }

    @ParameterizedTest
    @ValueSource(strings = {"https://example.com", "HTTP://Example.com/path?query=value"})
    void acceptsHttpUrlsWithHosts(String url) {
        assertThatCode(() -> urlValidator.validate(url)).doesNotThrowAnyException();
    }
}
