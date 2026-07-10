package com.bankcard.paymentservice.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class FraudDetectionProducer {

    private static final Logger log = LoggerFactory.getLogger(FraudDetectionProducer.class);

    private final KafkaTemplate<String, FraudCheckEvent> kafkaTemplate;
    private final String topic;

    public FraudDetectionProducer(
            KafkaTemplate<String, FraudCheckEvent> kafkaTemplate,
            @Value("${app.kafka.topics.fraud-check:payment.fraud-check}") String topic
    ) {
        this.kafkaTemplate = kafkaTemplate;
        this.topic = topic;
    }

    public void publish(FraudCheckEvent event) {
        kafkaTemplate.send(topic, event.paymentId().toString(), event)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("Failed to publish fraud-check event for payment {}", event.paymentId(), ex);
                    } else {
                        log.info("Published fraud-check event for payment {} to partition {}",
                                event.paymentId(), result.getRecordMetadata().partition());
                    }
                });
    }
}
