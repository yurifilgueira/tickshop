package com.tickshop.payment.events.ticket;

import com.tickshop.payment.events.DomainEvent;

import java.time.Instant;
import java.util.UUID;

public sealed interface TicketEvent extends DomainEvent {

    record TicketReserved(
            UUID bookingId,
            UUID ticketId,
            Instant createdAt
    ) implements TicketEvent {}

    record TicketReservationFailed(
            UUID bookingId,
            String reason,
            Instant createdAt
    ) implements TicketEvent {}

    record TicketSold(
            UUID bookingId,
            UUID ticketId,
            Instant createdAt
    ) implements TicketEvent {}
}