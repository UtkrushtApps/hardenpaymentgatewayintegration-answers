package com.example.payments.mapper;

import com.example.payments.api.dto.PaymentRequestDTO;
import com.example.payments.api.dto.PaymentResponseDTO;
import com.example.payments.domain.Payment;

import org.springframework.stereotype.Component;

@Component
public class PaymentMapper {
    public PaymentResponseDTO toDTO(Payment payment) {
        return new PaymentResponseDTO(
                payment.getId(),
                payment.getStatus(),
                payment.getAmount(),
                payment.getCurrency(),
                payment.getUserId(),
                payment.getCreatedAt(),
                payment.getUpdatedAt(),
                payment.getFailureReason()
        );
    }

    public Payment toEntity(PaymentRequestDTO dto) {
        return new Payment(dto.getUserId(), dto.getAmount(), dto.getCurrency());
    }
}
