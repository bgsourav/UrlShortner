package com.bgsourav.urlshortener.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class LinkRedirectIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shortensThenRedirectsToTheStoredUrl() throws Exception {
        MvcResult shortenResult = mockMvc.perform(post("/shorten")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"url\":\"https://example.com/redirect-target\"}"))
                .andExpect(status().isCreated())
                .andReturn();
        JsonNode response = objectMapper.readTree(shortenResult.getResponse().getContentAsString());

        mockMvc.perform(get("/" + response.get("code").asText()))
                .andExpect(status().isMovedPermanently())
                .andExpect(header().string("Location", "https://example.com/redirect-target"));
    }

    @Test
    void returnsNotFoundForAnUnknownCode() throws Exception {
        mockMvc.perform(get("/missing"))
                .andExpect(status().isNotFound());
    }
}
