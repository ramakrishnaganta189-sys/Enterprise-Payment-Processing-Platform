package com.bankcard.paymentservice.controller;

import com.bankcard.paymentservice.PaymentServiceApplication;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * End-to-end style test exercising the full stack: login -> obtain JWT ->
 * call a protected endpoint with the token -> verify persistence via the
 * response body. Uses the in-memory H2 database and embedded config from
 * application-test.yml, so no external Postgres/Kafka is required to run it.
 */
@SpringBootTest(classes = PaymentServiceApplication.class, webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class PaymentControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void unauthenticatedRequest_isRejected() throws Exception {
        mockMvc.perform(get("/api/v1/payments").param("accountId", "acct-123"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void loginThenCreatePayment_succeeds() throws Exception {
        String loginBody = objectMapper.writeValueAsString(Map.of(
                "username", "demo-user",
                "password", "demo-pass"
        ));

        String loginResponse = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType("application/json")
                        .content(loginBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").exists())
                .andReturn().getResponse().getContentAsString();

        String token = objectMapper.readTree(loginResponse).get("accessToken").asText();

        String paymentBody = objectMapper.writeValueAsString(Map.of(
                "accountId", "acct-123",
                "merchantId", "merchant-456",
                "amount", new BigDecimal("42.50"),
                "currency", "USD"
        ));

        mockMvc.perform(post("/api/v1/payments")
                        .header("Authorization", "Bearer " + token)
                        .contentType("application/json")
                        .content(paymentBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.accountId").value("acct-123"))
                .andExpect(jsonPath("$.status").value("PENDING"));
    }
}
