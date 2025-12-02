package com.example.payments.service;

import com.example.payments.api.dto.PaymentRequestDTO;
import com.example.payments.domain.Payment;
import com.example.payments.domain.PaymentStatus;
import com.example.payments.exception.PaymentNotFoundException;
import com.example.payments.mapper.PaymentMapper;
import com.example.payments.repository.PaymentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class PaymentService {
    private static final Logger logger = LoggerFactory.getLogger(PaymentService.class);

    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;
    private final AsyncPaymentProcessor asyncPaymentProcessor;

    public PaymentService(PaymentRepository paymentRepository, PaymentMapper paymentMapper, AsyncPaymentProcessor asyncPaymentProcessor) {
        this.paymentRepository = paymentRepository;
        this.paymentMapper = paymentMapper;
        this.asyncPaymentProcessor = asyncPaymentProcessor;
    }

    @Transactional
    public Payment initiatePayment(PaymentRequestDTO paymentRequestDTO) {
        Payment payment = paymentMapper.toEntity(paymentRequestDTO);
        payment.setStatus(PaymentStatus.PENDING);
        payment.setCreatedAt(java.time.OffsetDateTime.now());
        payment.setUpdatedAt(java.time.OffsetDateTime.now());
        Payment saved = paymentRepository.save(payment);
        asyncPaymentProcessor.processPayment(saved);
        return saved;
    }

    public Payment getPayment(String paymentId) {
        Optional<Payment> payment = paymentRepository.findById(paymentId);
        return payment.orElseThrow(() -> new PaymentNotFoundException(paymentId));
    }
}