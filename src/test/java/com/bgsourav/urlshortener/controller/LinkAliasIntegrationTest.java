package com.bgsourav.urlshortener.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class LinkAliasIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void createsALinkWithTheRequestedAlias() throws Exception {
        mockMvc.perform(post("/shorten")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"url\":\"https://example.com/alias-target\",\"alias\":\"my_link-1\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").value("my_link-1"));
    }

    @Test
    void rejectsAnAliasThatIsAlreadyTaken() throws Exception {
        String firstRequest = "{\"url\":\"https://example.com/first\",\"alias\":\"taken_alias\"}";
        String secondRequest = "{\"url\":\"https://example.com/second\",\"alias\":\"taken_alias\"}";

        mockMvc.perform(post("/shorten").contentType(MediaType.APPLICATION_JSON).content(firstRequest))
                .andExpect(status().isCreated());
        mockMvc.perform(post("/shorten").contentType(MediaType.APPLICATION_JSON).content(secondRequest))
                .andExpect(status().isConflict());
    }

    @Test
    void rejectsAReservedAlias() throws Exception {
        mockMvc.perform(post("/shorten")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"url\":\"https://example.com/reserved\",\"alias\":\"health\"}"))
                .andExpect(status().isBadRequest());
    }
}
