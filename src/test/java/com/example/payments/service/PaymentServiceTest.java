package com.example.payments.service;

import com.example.payments.api.dto.PaymentRequestDTO;
import com.example.payments.domain.Payment;
import com.example.payments.domain.PaymentStatus;
import com.example.payments.mapper.PaymentMapper;
import com.example.payments.repository.PaymentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class PaymentServiceTest {
    @Mock
    PaymentRepository paymentRepository;
    @Mock
    PaymentMapper paymentMapper;
    @Mock
    AsyncPaymentProcessor asyncPaymentProcessor;
    @InjectMocks
    PaymentService paymentService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testInitiatePaymentHappyPath() {
        PaymentRequestDTO dto = new PaymentRequestDTO(new BigDecimal("100.00"), "INR", "user789");
        Payment payment = new Payment("user789", new BigDecimal("100.00"), "INR");
        when(paymentMapper.toEntity(any(PaymentRequestDTO.class))).thenReturn(payment);
        when(paymentRepository.save(any(Payment.class))).then(returnsFirstArg());

        Payment result = paymentService.initiatePayment(dto);

        assertNotNull(result);
        assertEquals(PaymentStatus.PENDING, result.getStatus());
        verify(asyncPaymentProcessor).processPayment(any(Payment.class));
    }

    @Test
    void testGetPaymentNotFound() {
        when(paymentRepository.findById(any())).thenReturn(Optional.empty());
        assertThrows(com.example.payments.exception.PaymentNotFoundException.class, () -> paymentService.getPayment("123"));
    }
}
