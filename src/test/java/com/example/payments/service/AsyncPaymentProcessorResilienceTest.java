package com.example.payments.service;

import com.example.payments.domain.Payment;
import com.example.payments.domain.PaymentStatus;
import com.example.payments.repository.PaymentRepository;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.scheduling.annotation.AsyncResult;

import java.math.BigDecimal;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AsyncPaymentProcessorResilienceTest {
    @Mock
    PaymentGatewayClient paymentGatewayClient;
    @Mock
    PaymentRepository paymentRepository;
    @Mock
    CircuitBreakerRegistry circuitBreakerRegistry;
    @Mock
    CircuitBreaker circuitBreaker;
    @InjectMocks
    AsyncPaymentProcessor asyncPaymentProcessor;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(circuitBreakerRegistry.circuitBreaker(anyString())).thenReturn(circuitBreaker);
    }

    @Test
    void testGatewayDownWithFallback() {
        Payment payment = new Payment("userX", new BigDecimal("500.00"), "USD");
        payment.setId("PAY-FAIL-1");
        doThrow(new RuntimeException("Gateway unavailable"))
                .when(paymentGatewayClient).charge(any(Payment.class));

        // Should not throw, sets to PENDING with failureReason
        asyncPaymentProcessor.processPayment(payment);
        assertEquals(PaymentStatus.PENDING, payment.getStatus());
        assertEquals("Gateway unavailable", payment.getFailureReason());
        verify(paymentRepository).save(payment);
    }
}
