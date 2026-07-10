package com.bankcard.paymentservice.service.impl;

import com.bankcard.paymentservice.dto.PaymentRequest;
import com.bankcard.paymentservice.dto.PaymentResponse;
import com.bankcard.paymentservice.exception.PaymentNotFoundException;
import com.bankcard.paymentservice.kafka.FraudCheckEvent;
import com.bankcard.paymentservice.kafka.FraudDetectionProducer;
import com.bankcard.paymentservice.model.Payment;
import com.bankcard.paymentservice.repository.PaymentRepository;
import com.bankcard.paymentservice.service.PaymentService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final FraudDetectionProducer fraudDetectionProducer;

    public PaymentServiceImpl(PaymentRepository paymentRepository, FraudDetectionProducer fraudDetectionProducer) {
        this.paymentRepository = paymentRepository;
        this.fraudDetectionProducer = fraudDetectionProducer;
    }

    @Override
    @Transactional
    public PaymentResponse createPayment(PaymentRequest request) {
        Payment payment = new Payment(
                request.accountId(),
                request.merchantId(),
                request.amount(),
                request.currency().toUpperCase()
        );

        Payment saved = paymentRepository.save(payment);

        // Publish asynchronously so the fraud-detection pipeline can score
        // the transaction without blocking the caller's response.
        fraudDetectionProducer.publish(new FraudCheckEvent(
                saved.getId(),
                saved.getAccountId(),
                saved.getMerchantId(),
                saved.getAmount(),
                saved.getCurrency(),
                Instant.now()
        ));

        return PaymentResponse.from(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentResponse getPayment(UUID id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new PaymentNotFoundException(id));
        return PaymentResponse.from(payment);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentResponse> getPaymentsForAccount(String accountId) {
        return paymentRepository.findByAccountId(accountId).stream()
                .map(PaymentResponse::from)
                .toList();
    }
}
