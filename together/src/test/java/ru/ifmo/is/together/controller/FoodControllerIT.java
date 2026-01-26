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
import ru.ifmo.is.together.food.Food;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class FoodControllerIT extends IntegrationTestBase {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private String jwtToken;

    @BeforeEach
    void setUp() throws Exception {
        // Create a test user and get JWT token
        RegisterRequest registerRequest = RegisterRequest.builder()
                .firstname("Food")
                .lastname("Chef")
                .email("food.chef@example.com")
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
    void testGetAllFoods() throws Exception {
        mockMvc.perform(get("/foods/all")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void testGetFoodById() throws Exception {
        mockMvc.perform(get("/foods/1")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void testCreateFood() throws Exception {
        Food food = new Food();
        food.setName("Test Food");
        food.setDescription("A test food");
        food.setPrice(200.0);

        mockMvc.perform(post("/foods/create")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(food)))
                .andExpect(status().isOk());
    }

    @Test
    void testUpdateFood() throws Exception {
        Food updatedFood = new Food();
        updatedFood.setName("Updated Food");
        updatedFood.setDescription("An updated food");
        updatedFood.setPrice(250.0);

        mockMvc.perform(put("/foods/update/1")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedFood)))
                .andExpect(status().isOk());
    }

    @Test
    void testDeleteFood() throws Exception {
        mockMvc.perform(delete("/foods/delete/1")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken))
                .andExpect(status().isOk());
    }
}