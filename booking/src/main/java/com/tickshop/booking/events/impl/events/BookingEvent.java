package com.tickshop.booking.events.impl.events;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.tickshop.booking.events.DomainEvent;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "eventType"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = BookingEvent.BookingCreated.class, name = "BookingCreated"),
        @JsonSubTypes.Type(value = BookingEvent.BookingCompleted.class, name = "BookingCompleted"),
        @JsonSubTypes.Type(value = BookingEvent.BookingCancelled.class, name = "BookingCancelled")
})
public sealed interface BookingEvent extends DomainEvent permits BookingEvent.BookingCreated, BookingEvent.BookingCompleted, BookingEvent.BookingCancelled {

    record BookingCreated(
            UUID bookingId,
            UUID customerId,
            UUID showId,
            Integer ticketsQtt,
            BigDecimal price,
            Instant createdAt
    ) implements BookingEvent {}

    record BookingCompleted(
            UUID bookingId,
            Instant createdAt
    ) implements BookingEvent {}

    record BookingCancelled(
            UUID bookingId,
            Instant createdAt,
            String reason
    ) implements BookingEvent {}
}
