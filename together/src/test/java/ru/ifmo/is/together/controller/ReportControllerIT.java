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
import ru.ifmo.is.together.reports.Report;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class ReportControllerIT extends IntegrationTestBase {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private String jwtToken;

    @BeforeEach
    void setUp() throws Exception {
        // Create a test user and get JWT token
        RegisterRequest registerRequest = RegisterRequest.builder()
                .firstname("Report")
                .lastname("Analyst")
                .email("report.analyst@example.com")
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
    void testGetAllReports() throws Exception {
        mockMvc.perform(get("/reports/all")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void testGetReportById() throws Exception {
        mockMvc.perform(get("/reports/1")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void testCreateReport() throws Exception {
        Report report = new Report();
        report.setTitle("Test Report");
        report.setDescription("A test report");
        report.setType("Bug");

        mockMvc.perform(post("/reports/create")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(report)))
                .andExpect(status().isOk());
    }

    @Test
    void testUpdateReport() throws Exception {
        Report updatedReport = new Report();
        updatedReport.setTitle("Updated Report");
        updatedReport.setDescription("An updated report");
        updatedReport.setType("Feature");

        mockMvc.perform(put("/reports/update/1")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedReport)))
                .andExpect(status().isOk());
    }

    @Test
    void testDeleteReport() throws Exception {
        mockMvc.perform(delete("/reports/delete/1")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken))
                .andExpect(status().isOk());
    }
}