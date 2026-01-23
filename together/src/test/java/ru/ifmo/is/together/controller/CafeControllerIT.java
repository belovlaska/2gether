package ru.ifmo.is.together.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.ifmo.is.together.IntegrationTestBase;
import ru.ifmo.is.together.auth.RegisterRequest;
import ru.ifmo.is.together.cafe.Cafe;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class CafeControllerIT extends IntegrationTestBase {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private String jwtToken;

    @BeforeEach
    void setUp() throws Exception {
        // Create a test user and get JWT token
        RegisterRequest registerRequest = RegisterRequest.builder()
                .firstname("Cafe")
                .lastname("Owner")
                .email("cafe.owner@example.com")
                .password("password123")
                .build();

        var registerResponse = mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isOk())
                .andReturn();

        // Extract token from response
        String responseContent = registerResponse.getResponse().getContentAsString();
        jwtToken = responseContent.substring(responseContent.indexOf("\"") + 1, responseContent.lastIndexOf("\""));
    }

    @Test
    void testGetAllCafes() throws Exception {
        mockMvc.perform(get("/cafes/all")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void testGetCafeById() throws Exception {
        mockMvc.perform(get("/cafes/1")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void testCreateCafe() throws Exception {
        Cafe cafe = new Cafe();
        cafe.setName("Test Cafe");
        cafe.setDescription("A test cafe");
        cafe.setLocation("Test Location");

        mockMvc.perform(post("/cafes/create")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cafe)))
                .andExpect(status().isOk());
    }

    @Test
    void testUpdateCafe() throws Exception {
        Cafe updatedCafe = new Cafe();
        updatedCafe.setName("Updated Cafe");
        updatedCafe.setDescription("An updated cafe");
        updatedCafe.setLocation("Updated Location");

        mockMvc.perform(put("/cafes/update/1")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedCafe)))
                .andExpect(status().isOk());
    }

    @Test
    void testDeleteCafe() throws Exception {
        mockMvc.perform(delete("/cafes/delete/1")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken))
                .andExpect(status().isOk());
    }
}