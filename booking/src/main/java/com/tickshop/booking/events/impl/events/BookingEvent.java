package com.tickshop.booking.events.impl.events;

import com.tickshop.booking.events.DomainEvent;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

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
