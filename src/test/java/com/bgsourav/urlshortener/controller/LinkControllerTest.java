package com.bgsourav.urlshortener.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.bgsourav.urlshortener.dto.ShortenResponse;
import com.bgsourav.urlshortener.service.LinkService;

@WebMvcTest(LinkController.class)
class LinkControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private LinkService linkService;

    @Test
    void createsAShortLink() throws Exception {
        ShortenResponse response = new ShortenResponse(
                "abc1234", "http://localhost:8080/abc1234", "https://example.com/page");
        org.mockito.Mockito.when(linkService.create(any())).thenReturn(response);

        mockMvc.perform(post("/shorten")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"url\":\"https://example.com/page\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").value("abc1234"))
                .andExpect(jsonPath("$.shortUrl").value("http://localhost:8080/abc1234"))
                .andExpect(jsonPath("$.longUrl").value("https://example.com/page"));
    }

    @Test
    void rejectsAnInvalidAliasBeforeCallingTheService() throws Exception {
        mockMvc.perform(post("/shorten")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"url\":\"https://example.com/page\",\"alias\":\"health\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("alias must be 5-32 characters using letters, digits, underscores, or hyphens"));

        verifyNoInteractions(linkService);
    }
}
