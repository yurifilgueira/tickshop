package com.tickshop.booking.events.tickets;


import com.tickshop.booking.events.DomainEvent;

import java.time.Instant;
import java.util.UUID;

public sealed interface TicketEvent extends DomainEvent permits TicketEvent.TicketReserved, TicketEvent.TicketReservationFailed, TicketEvent.TicketSold {

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