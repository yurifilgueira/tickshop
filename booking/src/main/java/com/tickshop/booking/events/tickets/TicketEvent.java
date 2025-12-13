package com.tickshop.booking.events.tickets;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.tickshop.booking.events.DomainEvent;

import java.time.Instant;
import java.util.UUID;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "eventType"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = TicketEvent.TicketReserved.class, name = "TicketReserved"),
        @JsonSubTypes.Type(value = TicketEvent.TicketReservationFailed.class, name = "TicketReservationFailed"),
        @JsonSubTypes.Type(value = TicketEvent.TicketSold.class, name = "TicketSold")
})
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