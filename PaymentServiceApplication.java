package com.bankcard.paymentservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point for the Payment Processing microservice.
 *
 * This sample models the kind of secure, high-volume payment API described in
 * the "Sr. Java Full Stack Developer" experience: Java 21 + Spring Boot,
 * JWT/OAuth2-secured REST endpoints, Spring Data JPA persistence, and
 * Kafka-based event streaming for downstream fraud-detection workflows.
 */
@SpringBootApplication
public class PaymentServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(PaymentServiceApplication.class, args);
    }
}
