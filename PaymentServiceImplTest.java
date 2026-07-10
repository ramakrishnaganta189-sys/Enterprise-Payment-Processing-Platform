package com.bankcard.paymentservice.service;

import com.bankcard.paymentservice.dto.PaymentRequest;
import com.bankcard.paymentservice.dto.PaymentResponse;
import com.bankcard.paymentservice.exception.PaymentNotFoundException;
import com.bankcard.paymentservice.kafka.FraudDetectionProducer;
import com.bankcard.paymentservice.model.Payment;
import com.bankcard.paymentservice.model.PaymentStatus;
import com.bankcard.paymentservice.repository.PaymentRepository;
import com.bankcard.paymentservice.service.impl.PaymentServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceImplTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private FraudDetectionProducer fraudDetectionProducer;

    private PaymentService paymentService;

    @BeforeEach
    void setUp() {
        paymentService = new PaymentServiceImpl(paymentRepository, fraudDetectionProducer);
    }

    @Test
    void createPayment_savesEntityAndPublishesFraudCheckEvent() {
        PaymentRequest request = new PaymentRequest("acct-123", "merchant-456", new BigDecimal("250.00"), "usd");

        when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        PaymentResponse response = paymentService.createPayment(request);

        assertThat(response.accountId()).isEqualTo("acct-123");
        assertThat(response.currency()).isEqualTo("USD");
        assertThat(response.status()).isEqualTo(PaymentStatus.PENDING);

        ArgumentCaptor<Payment> paymentCaptor = ArgumentCaptor.forClass(Payment.class);
        verify(paymentRepository).save(paymentCaptor.capture());
        assertThat(paymentCaptor.getValue().getAmount()).isEqualByComparingTo("250.00");

        verify(fraudDetectionProducer, times(1)).publish(any());
    }

    @Test
    void getPayment_throwsWhenNotFound() {
        UUID missingId = UUID.randomUUID();
        when(paymentRepository.findById(missingId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> paymentService.getPayment(missingId))
                .isInstanceOf(PaymentNotFoundException.class);
    }

    @Test
    void getPaymentsForAccount_mapsEntitiesToResponses() {
        Payment payment = new Payment("acct-789", "merchant-001", new BigDecimal("99.99"), "USD");
        when(paymentRepository.findByAccountId("acct-789")).thenReturn(List.of(payment));

        List<PaymentResponse> results = paymentService.getPaymentsForAccount("acct-789");

        assertThat(results).hasSize(1);
        assertThat(results.get(0).accountId()).isEqualTo("acct-789");
    }
}
