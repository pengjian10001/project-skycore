package com.skycore.boot.open;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasKey;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class WbNbIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void wb001ThenDashboard() throws Exception {
        String body = """
                {
                  "payloadId": "PL-IT-001",
                  "satTime": 1710000000000,
                  "magX": 1.0,
                  "magY": 2.0,
                  "magZ": 2.0,
                  "status": 1,
                  "rawDigest": "AABB"
                }
                """;
        mockMvc.perform(post("/api/open/wb/001/payload-data")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.transId").isNotEmpty())
                .andExpect(jsonPath("$.data.forwarded").value(hasKey("magTotal")));

        mockMvc.perform(get("/api/open/wb/005/dashboard"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.storeCount").value(1));
    }

    @Test
    void wb003Forward() throws Exception {
        String body = """
                {
                  "cmdTime": 1710000000000,
                  "cmdSeq": 9,
                  "targetId": 2,
                  "cmdCode": 1,
                  "workMode": 1,
                  "sampleFreq": 5.0,
                  "exposure": 0.5
                }
                """;
        mockMvc.perform(post("/api/open/wb/003/sim-command")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.forwarded.targetId").value(2))
                .andExpect(jsonPath("$.data.packedSimHex").isNotEmpty());
    }

    @Test
    void validationFails() throws Exception {
        mockMvc.perform(post("/api/open/wb/001/payload-data")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400));
    }
}
