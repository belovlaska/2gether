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
import ru.ifmo.is.together.drink.Drink;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class DrinkControllerIT extends IntegrationTestBase {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private String jwtToken;

    @BeforeEach
    void setUp() throws Exception {
        // Create a test user and get JWT token
        RegisterRequest registerRequest = RegisterRequest.builder()
                .firstname("Drink")
                .lastname("Manager")
                .email("drink.manager@example.com")
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
    void testGetAllDrinks() throws Exception {
        mockMvc.perform(get("/drinks/all")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void testGetDrinkById() throws Exception {
        mockMvc.perform(get("/drinks/1")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void testCreateDrink() throws Exception {
        Drink drink = new Drink();
        drink.setName("Test Drink");
        drink.setDescription("A test drink");
        drink.setPrice(100.0);

        mockMvc.perform(post("/drinks/create")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(drink)))
                .andExpect(status().isOk());
    }

    @Test
    void testUpdateDrink() throws Exception {
        Drink updatedDrink = new Drink();
        updatedDrink.setName("Updated Drink");
        updatedDrink.setDescription("An updated drink");
        updatedDrink.setPrice(150.0);

        mockMvc.perform(put("/drinks/update/1")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedDrink)))
                .andExpect(status().isOk());
    }

    @Test
    void testDeleteDrink() throws Exception {
        mockMvc.perform(delete("/drinks/delete/1")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken))
                .andExpect(status().isOk());
    }
}