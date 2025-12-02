package com.example.payments.api.controller;

import com.example.payments.api.dto.PaymentRequestDTO;
import com.example.payments.api.dto.PaymentResponseDTO;
import com.example.payments.mapper.PaymentMapper;
import com.example.payments.service.PaymentService;
import com.example.payments.domain.Payment;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {
    private static final Logger logger = LoggerFactory.getLogger(PaymentController.class);
    private final PaymentService paymentService;
    private final PaymentMapper paymentMapper;

    public PaymentController(PaymentService paymentService, PaymentMapper paymentMapper) {
        this.paymentService = paymentService;
        this.paymentMapper = paymentMapper;
    }

    @PostMapping
    public ResponseEntity<PaymentResponseDTO> initiatePayment(@Valid @RequestBody PaymentRequestDTO requestDto) {
        logger.info("Received payment creation request for user={} amount={}", requestDto.getUserId(), requestDto.getAmount());
        Payment payment = paymentService.initiatePayment(requestDto);
        return ResponseEntity.ok(paymentMapper.toDTO(payment));
    }

    @GetMapping("/{paymentId}")
    public ResponseEntity<PaymentResponseDTO> getPayment(@PathVariable String paymentId) {
        Payment payment = paymentService.getPayment(paymentId);
        return ResponseEntity.ok(paymentMapper.toDTO(payment));
    }
}
