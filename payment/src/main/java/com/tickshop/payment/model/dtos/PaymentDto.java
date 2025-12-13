package com.tickshop.payment.model.dtos;

import com.tickshop.payment.model.enums.PaymentStatus;

import java.time.Instant;
import java.math.BigDecimal;
import java.util.UUID;

public record PaymentDto(
        UUID paymentId,
        UUID bookingId,
        UUID customerId,
        BigDecimal amount,
        PaymentStatus status,
        Instant createdAt
) {
}
