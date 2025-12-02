# Solution Steps

1. Define clean DTOs for the payment API input (PaymentRequestDTO) and output (PaymentResponseDTO), with validation annotations.

2. Implement a PaymentMapper to map between DTOs and domain/entity objects.

3. Refactor PaymentController to use DTOs and delegate to PaymentService, returning only DTOs to clients.

4. In PaymentService, change initiatePayment to create a PENDING Payment, save it, and immediately offload processing to an AsyncPaymentProcessor. Main thread returns quickly with the created payment as PENDING.

5. Create AsyncPaymentProcessor, which receives the Payment, and asynchronously calls PaymentGatewayClient. On success/failure, updates the Payment entity status and saves it.

6. Implement PaymentGatewayClient to call the external gateway, protected by Resilience4j annotations: @CircuitBreaker, @Retry, @TimeLimiter, and provide a fallback method. Log all outcomes and circuit states.

7. Add sensible Resilience4j config beans and use Spring properties for gateway endpoint.

8. Enhance Payment domain/entity to capture status transitions and failure reasons. Ensure PENDING for fallback and mark as FAILED for definitive gateway failures.

9. Refactor global exception handler to provide a structured error response using ApiError, catching validation, not found, and general exceptions.

10. Enable async execution using @EnableAsync and provide a dedicated thread pool for payment processing.

11. Write initial unit tests for PaymentService (happy path/initiation and get), and for AsyncPaymentProcessor to exercise fallback handling (gateway failures).

12. Add sufficient logging throughout to ensure all API calls and status transitions are traceable, and log circuit breaker states for observability.

13. Ensure proper dependency injection/wiring/configuration for all beans and that properties (like payment.gateway.url) are used via @Value.

14. Provide PaymentController GET method to fetch payment status by ID for polling/tracking.

15. Check thread safety and transactional boundaries in async processing: only update payment in the async executor, not in the main thread.

