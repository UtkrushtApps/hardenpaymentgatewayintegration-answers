package com.example.payments.service;

import com.example.payments.domain.Payment;
import com.example.payments.domain.PaymentStatus;
import com.example.payments.repository.PaymentRepository;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class AsyncPaymentProcessor {
    private static final Logger logger = LoggerFactory.getLogger(AsyncPaymentProcessor.class);
    private final PaymentGatewayClient gatewayClient;
    private final PaymentRepository paymentRepository;
    private final CircuitBreaker circuitBreaker;

    public AsyncPaymentProcessor(PaymentGatewayClient gatewayClient, PaymentRepository paymentRepository, CircuitBreakerRegistry circuitBreakerRegistry) {
        this.gatewayClient = gatewayClient;
        this.paymentRepository = paymentRepository;
        this.circuitBreaker = circuitBreakerRegistry.circuitBreaker("paymentGateway");
    }

    @Async("paymentExecutor")
    @Transactional
    public void processPayment(Payment payment) {
        logger.info("Processing payment asynchronously: paymentId={}", payment.getId());
        try {
            boolean result = gatewayClient.charge(payment);
            payment.setStatus(result ? PaymentStatus.SUCCESS : PaymentStatus.FAILED);
            payment.setFailureReason(result ? null : "Gateway returned failure response");
        } catch (Exception e) {
            logger.warn("Payment gateway call failed for paymentId={} : {}", payment.getId(), e.getMessage());
            payment.setStatus(PaymentStatus.PENDING);
            payment.setFailureReason("Gateway unavailable");
        } finally {
            payment.setUpdatedAt(java.time.OffsetDateTime.now());
            paymentRepository.save(payment);
            logger.info("Payment updated: id={}, status={}, circuitBreakerState={}", payment.getId(), payment.getStatus(), circuitBreaker.getState());
        }
    }
}
