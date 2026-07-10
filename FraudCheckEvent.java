package com.bankcard.paymentservice.kafka;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * Lightweight event published whenever a payment is created. A downstream
 * fraud-detection consumer (rules engine, ML model, or LLM-based anomaly
 * scoring service) subscribes to this topic and can flag/decline the
 * payment asynchronously.
 */
public record FraudCheckEvent(
        UUID paymentId,
        String accountId,
        String merchantId,
        BigDecimal amount,
        String currency,
        Instant occurredAt
) {
}
