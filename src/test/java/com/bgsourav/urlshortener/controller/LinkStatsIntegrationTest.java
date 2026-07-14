package com.bgsourav.urlshortener.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
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
class LinkStatsIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void returnsStoredLinkStats() throws Exception {
        MvcResult shortenResult = mockMvc.perform(post("/shorten")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"url\":\"https://example.com/stats-target\"}"))
                .andExpect(status().isCreated())
                .andReturn();
        JsonNode shortenResponse = objectMapper.readTree(shortenResult.getResponse().getContentAsString());
        String code = shortenResponse.get("code").asText();

        mockMvc.perform(get("/" + code)).andExpect(status().isMovedPermanently());

        mockMvc.perform(get("/" + code + "/stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(code))
                .andExpect(jsonPath("$.longUrl").value("https://example.com/stats-target"))
                .andExpect(jsonPath("$.clicks").value(1))
                .andExpect(jsonPath("$.createdAt").isNotEmpty())
                .andExpect(jsonPath("$.lastAccessedAt").isNotEmpty());
    }

    @Test
    void returnsNotFoundForUnknownCode() throws Exception {
        mockMvc.perform(get("/missing/stats"))
                .andExpect(status().isNotFound());
    }
}
