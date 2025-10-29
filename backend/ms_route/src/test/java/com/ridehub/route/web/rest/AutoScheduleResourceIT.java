package com.ridehub.route.web.rest;

import com.ridehub.route.service.AutoScheduleService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration tests for the {@link AutoScheduleResource} REST controller.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebMvc
@ActiveProfiles("testdev")
class AutoScheduleResourceIT {

    @Autowired
    private MockMvc restAutoScheduleMockMvc;

    @Autowired
    private AutoScheduleService autoScheduleService;

    @Test
    void testTriggerAutoSchedule() throws Exception {
        restAutoScheduleMockMvc.perform(MockMvcRequestBuilders.post("/api/auto-schedule/trigger")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void testGetAutoScheduleStatus() throws Exception {
        restAutoScheduleMockMvc.perform(MockMvcRequestBuilders.get("/api/auto-schedule/status")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.cronExpression").exists());
    }
}