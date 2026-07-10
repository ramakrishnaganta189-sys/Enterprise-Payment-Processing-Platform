package com.bankcard.paymentservice.repository;

import com.bankcard.paymentservice.model.Payment;
import com.bankcard.paymentservice.model.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface PaymentRepository extends JpaRepository<Payment, UUID> {

    List<Payment> findByAccountId(String accountId);

    List<Payment> findByStatus(PaymentStatus status);
}
