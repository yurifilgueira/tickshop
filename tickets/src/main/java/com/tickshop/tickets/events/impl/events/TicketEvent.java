package com.tickshop.tickets.events.impl.events;

import com.tickshop.tickets.events.DomainEvent;

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

    record TicketReleased(
            UUID bookingId,
            UUID ticketId,
            Instant createdAt
    ) implements TicketEvent {}
}