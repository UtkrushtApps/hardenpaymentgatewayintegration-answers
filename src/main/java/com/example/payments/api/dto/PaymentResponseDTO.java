package com.example.payments.api.dto;

import com.example.payments.domain.PaymentStatus;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public class PaymentResponseDTO {
    private String paymentId;
    private PaymentStatus status;
    private BigDecimal amount;
    private String currency;
    private String userId;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
    private String failureReason;

    public PaymentResponseDTO(String paymentId, PaymentStatus status, BigDecimal amount, String currency, String userId, OffsetDateTime createdAt, OffsetDateTime updatedAt, String failureReason) {
        this.paymentId = paymentId;
        this.status = status;
        this.amount = amount;
        this.currency = currency;
        this.userId = userId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.failureReason = failureReason;
    }

    // Getters and setters omitted for brevity

    public String getPaymentId() { return paymentId; }
    public void setPaymentId(String paymentId) { this.paymentId = paymentId; }
    public PaymentStatus getStatus() { return status; }
    public void setStatus(PaymentStatus status) { this.status = status; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }
    public OffsetDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(OffsetDateTime updatedAt) { this.updatedAt = updatedAt; }
    public String getFailureReason() { return failureReason; }
    public void setFailureReason(String failureReason) { this.failureReason = failureReason; }
}
