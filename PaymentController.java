package com.bankcard.paymentservice.controller;

import com.bankcard.paymentservice.dto.PaymentRequest;
import com.bankcard.paymentservice.dto.PaymentResponse;
import com.bankcard.paymentservice.service.PaymentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping
    public ResponseEntity<PaymentResponse> createPayment(@Valid @RequestBody PaymentRequest request) {
        PaymentResponse response = paymentService.createPayment(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PaymentResponse> getPayment(@PathVariable UUID id) {
        return ResponseEntity.ok(paymentService.getPayment(id));
    }

    @GetMapping
    public ResponseEntity<List<PaymentResponse>> getPaymentsForAccount(@RequestParam String accountId) {
        return ResponseEntity.ok(paymentService.getPaymentsForAccount(accountId));
    }
}
