package com.tickshop.booking.services;



import com.tickshop.booking.events.DomainEvent;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public sealed interface PaymentEvent extends DomainEvent permits PaymentEvent.PaymentDeclined, PaymentEvent.PaymentProcessed, PaymentEvent.PaymentRefunded {

    record PaymentProcessed(
            UUID bookingId,
            UUID paymentId,
            BigDecimal total,
            Instant createdAt
    ) implements PaymentEvent {}

    record PaymentDeclined(
            UUID bookingId,
            String reason,
            Instant createdAt
    ) implements PaymentEvent {}

    record PaymentRefunded(
            UUID bookingId,
            UUID refundId,
            BigDecimal total,
            Instant createdAt
    ) implements PaymentEvent {}
}
