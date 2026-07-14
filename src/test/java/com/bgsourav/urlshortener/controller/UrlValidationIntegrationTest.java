package com.bgsourav.urlshortener.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.stream.Stream;

@SpringBootTest
@AutoConfigureMockMvc
class UrlValidationIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @ParameterizedTest
    @MethodSource("invalidUrls")
    void rejectsInvalidUrls(String url, String message) throws Exception {
        mockMvc.perform(post("/shorten")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"url\":\"" + url + "\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(message));
    }

    private static Stream<org.junit.jupiter.params.provider.Arguments> invalidUrls() {
        return Stream.of(
                org.junit.jupiter.params.provider.Arguments.of("javascript:alert(1)", "URL scheme must be http or https"),
                org.junit.jupiter.params.provider.Arguments.of("https:/missing-host", "URL must include a host"),
                org.junit.jupiter.params.provider.Arguments.of("https://example.com/" + "a".repeat(493), "URL must not exceed 512 characters"));
    }
}
