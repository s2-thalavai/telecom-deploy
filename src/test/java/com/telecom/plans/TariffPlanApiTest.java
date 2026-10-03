package com.telecom.plans;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("dev")
class TariffPlanApiTest {

    @Autowired
    MockMvc mvc;

    @Test
    void listsSeededPlans() throws Exception {
        mvc.perform(get("/api/v1/plans").with(httpBasic("dev", "dev123")))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.length()", greaterThanOrEqualTo(3)));
    }

    @Test
    void createsPlanAndServesV2() throws Exception {
        mvc.perform(post("/api/v1/plans").with(httpBasic("dev", "dev123"))
                   .contentType(MediaType.APPLICATION_JSON)
                   .content("""
                       {"code":"DATA99","name":"Data Pack 99","price":99,"dataGb":6,"validityDays":7,"category":"PREPAID"}
                       """))
           .andExpect(status().isCreated());

        mvc.perform(get("/api/v2/plans").with(httpBasic("dev", "dev123")))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$[0].pricePerDay").exists());
    }

    @Test
    void infoReportsEnvironment() throws Exception {
        mvc.perform(get("/actuator/info"))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.deployment.environment").value("dev"));
    }

    @Test
    void readinessProbeIsUp() throws Exception {
        mvc.perform(get("/actuator/health/readiness"))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.status").value("UP"));
    }
}
