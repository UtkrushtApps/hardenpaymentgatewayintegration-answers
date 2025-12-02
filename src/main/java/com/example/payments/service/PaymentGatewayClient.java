package com.example.payments.service;

import com.example.payments.domain.Payment;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.CompletableFuture;

@Component
public class PaymentGatewayClient {
    private static final Logger logger = LoggerFactory.getLogger(PaymentGatewayClient.class);

    private final RestTemplate restTemplate;
    private final String gatewayUrl;

    public PaymentGatewayClient(RestTemplate restTemplate,
                                @Value("${payment.gateway.url}") String gatewayUrl) {
        this.restTemplate = restTemplate;
        this.gatewayUrl = gatewayUrl;
    }

    @CircuitBreaker(name = "paymentGateway", fallbackMethod = "fallback")
    @Retry(name = "paymentGateway")
    @TimeLimiter(name = "paymentGateway")
    public boolean charge(Payment payment) {
        logger.info("Calling external payment API for paymentId={}", payment.getId());
        GatewayRequestBody body = new GatewayRequestBody(payment.getId(), payment.getAmount(), payment.getCurrency(), payment.getUserId());
        GatewayResponse response = restTemplate.postForObject(gatewayUrl, body, GatewayResponse.class);
        if (response != null && response.isSuccess()) {
            logger.info("Gateway call succeeded for paymentId={}", payment.getId());
            return true;
        } else {
            logger.warn("Gateway call failed, response not successful. paymentId={}", payment.getId());
            return false;
        }
    }

    public boolean fallback(Payment payment, Throwable throwable) {
        logger.error("Resilience4j fallback: could not process paymentId={}, cause={}", payment.getId(), throwable.toString());
        throw new RuntimeException("Payment gateway unavailable: " + throwable.getMessage());
    }

    private static class GatewayRequestBody {
        private String id;
        private java.math.BigDecimal amount;
        private String currency;
        private String userId;

        public GatewayRequestBody(String id, java.math.BigDecimal amount, String currency, String userId) {
            this.id = id;
            this.amount = amount;
            this.currency = currency;
            this.userId = userId;
        }
        // getters and setters omitted
    }

    private static class GatewayResponse {
        private boolean success;
        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }
        // For brevity, assuming only 'success' is relevant
    }
}
