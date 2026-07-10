package com.bankcard.paymentservice.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "payments", indexes = {
        @Index(name = "idx_payments_account_id", columnList = "accountId"),
        @Index(name = "idx_payments_status", columnList = "status")
})
public class Payment {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false)
    private String accountId;

    @Column(nullable = false)
    private String merchantId;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal amount;

    @Column(nullable = false, length = 3)
    private String currency;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    private Instant updatedAt;

    protected Payment() {
        // required by JPA
    }

    public Payment(String accountId, String merchantId, BigDecimal amount, String currency) {
        this.accountId = accountId;
        this.merchantId = merchantId;
        this.amount = amount;
        this.currency = currency;
        this.status = PaymentStatus.PENDING;
    }

    @PrePersist
    void onCreate() {
        this.createdAt = Instant.now();
        this.updatedAt = this.createdAt;
    }

    @PreUpdate
    void onUpdate() {
        this.updatedAt = Instant.now();
    }

    // Getters and setters

    public UUID getId() {
        return id;
    }

    public String getAccountId() {
        return accountId;
    }

    public String getMerchantId() {
        return merchantId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getCurrency() {
        return currency;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public void setStatus(PaymentStatus status) {
        this.status = status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}
