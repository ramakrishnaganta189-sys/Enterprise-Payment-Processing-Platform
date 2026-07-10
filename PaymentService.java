package com.bankcard.paymentservice.service;

import com.bankcard.paymentservice.dto.PaymentRequest;
import com.bankcard.paymentservice.dto.PaymentResponse;

import java.util.List;
import java.util.UUID;

public interface PaymentService {

    PaymentResponse createPayment(PaymentRequest request);

    PaymentResponse getPayment(UUID id);

    List<PaymentResponse> getPaymentsForAccount(String accountId);
}
