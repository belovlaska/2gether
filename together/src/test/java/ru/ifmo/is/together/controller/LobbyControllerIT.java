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
import ru.ifmo.is.together.lobby.Lobby;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class LobbyControllerIT extends IntegrationTestBase {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private String jwtToken;

    @BeforeEach
    void setUp() throws Exception {
        // Create a test user and get JWT token
        RegisterRequest registerRequest = RegisterRequest.builder()
                .firstname("Lobby")
                .lastname("Manager")
                .email("lobby.manager@example.com")
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
    void testGetAllLobbies() throws Exception {
        mockMvc.perform(get("/lobbies/all")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void testGetLobbyById() throws Exception {
        mockMvc.perform(get("/lobbies/1")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void testCreateLobby() throws Exception {
        Lobby lobby = new Lobby();
        lobby.setName("Test Lobby");
        lobby.setDescription("A test lobby");
        lobby.setMaxPlayers(4);

        mockMvc.perform(post("/lobbies/create")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(lobby)))
                .andExpect(status().isOk());
    }

    @Test
    void testUpdateLobby() throws Exception {
        Lobby updatedLobby = new Lobby();
        updatedLobby.setName("Updated Lobby");
        updatedLobby.setDescription("An updated lobby");
        updatedLobby.setMaxPlayers(8);

        mockMvc.perform(put("/lobbies/update/1")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedLobby)))
                .andExpect(status().isOk());
    }

    @Test
    void testDeleteLobby() throws Exception {
        mockMvc.perform(delete("/lobbies/delete/1")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken))
                .andExpect(status().isOk());
    }
}