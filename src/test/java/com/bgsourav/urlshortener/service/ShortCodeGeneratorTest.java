package com.bgsourav.urlshortener.service;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class ShortCodeGeneratorTest {

    private final ShortCodeGenerator shortCodeGenerator = new ShortCodeGenerator();

    @Test
    void generatesBase62CodesWithTheExpectedLength() {
        String code = shortCodeGenerator.generate();

        assertThat(code).hasSize(7).matches("[A-Za-z0-9]+");
    }

    @Test
    void generatesDifferentCodesOnRepeatedCalls() {
        String firstCode = shortCodeGenerator.generate();
        String secondCode = shortCodeGenerator.generate();

        assertThat(firstCode).isNotEqualTo(secondCode);
    }
}
