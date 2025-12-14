package com.tickshop.tickets.events.payment.events;


import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.tickshop.tickets.events.DomainEvent;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "eventType"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = PaymentEvent.PaymentDeclined.class, name = "PaymentDeclined"),
        @JsonSubTypes.Type(value = PaymentEvent.PaymentProcessed.class, name = "PaymentProcessed"),
        @JsonSubTypes.Type(value = PaymentEvent.PaymentRefunded.class, name = "PaymentRefunded")
})
public sealed interface PaymentEvent extends DomainEvent permits PaymentEvent.PaymentDeclined, PaymentEvent.PaymentProcessed, PaymentEvent.PaymentRefunded {

    record PaymentProcessed(
            UUID bookingId,
            UUID paymentId,
            UUID customerId,
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
