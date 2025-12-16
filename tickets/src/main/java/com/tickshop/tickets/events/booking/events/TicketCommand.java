package com.tickshop.tickets.events.booking.events;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.math.BigDecimal;
import java.util.UUID;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "commandType"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = TicketCommand.ReserveTicketCommand.class, name = "ReserveTicketCommand"),
        @JsonSubTypes.Type(value = TicketCommand.CancelReservationCommand.class, name = "CancelReservationCommand"),
        @JsonSubTypes.Type(value = TicketCommand.SellTicketCommand.class, name = "SellTicketCommand")
})
public sealed interface TicketCommand permits TicketCommand.CancelReservationCommand, TicketCommand.ReserveTicketCommand, TicketCommand.SellTicketCommand {

    record ReserveTicketCommand(
            UUID bookingId,
            UUID customerId,
            UUID showId,
            BigDecimal amount,
            Integer quantity
    ) implements TicketCommand {}

    record SellTicketCommand(
            UUID bookingId
    ) implements TicketCommand {}

    record CancelReservationCommand(
            UUID bookingId,
            String reason
    ) implements TicketCommand {}
}