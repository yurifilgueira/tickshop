package com.tickshop.booking.model.mappers;

import com.tickshop.booking.events.commands.TicketCommand;
import com.tickshop.booking.model.dtos.requests.CreateBookingRequest;
import com.tickshop.booking.model.enities.Booking;

public class TicketCommandMapper {

    public static TicketCommand.ReserveTicketCommand toReserveTicketCommand(Booking entity) {
        return new TicketCommand.ReserveTicketCommand(
                entity.getBookingId(),
                entity.getCustomerId(),
                entity.getShowId(),
                entity.getAmount(),
                entity.getQuantity()
        );
    }

    public static Booking dtoToEntity(CreateBookingRequest createBookingRequest) {
        return new Booking(
                createBookingRequest.customerId(),
                createBookingRequest.showId(),
                createBookingRequest.quantity(),
                createBookingRequest.amount()
        );
    }

}
