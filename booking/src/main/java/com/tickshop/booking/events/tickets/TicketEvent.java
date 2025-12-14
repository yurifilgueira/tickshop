package com.tickshop.booking.events.tickets;

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
        @JsonSubTypes.Type(value = TicketEvent.TicketReservationFailed.class, name = "TicketReservationFailed"),
        @JsonSubTypes.Type(value = TicketEvent.TicketSold.class, name = "TicketSold")
})
public sealed interface TicketEvent extends DomainEvent permits TicketEvent.TicketReservationFailed, TicketEvent.TicketSold {

    record TicketReservationFailed(
            UUID bookingId,
            String reason,
            Instant createdAt
    ) implements TicketEvent {}

    record TicketSold(
            UUID bookingId,
            UUID ticketId,
            UUID customerId,
            BigDecimal amount,
            Instant createdAt
    ) implements TicketEvent {}
}