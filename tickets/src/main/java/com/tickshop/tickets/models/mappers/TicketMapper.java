package com.tickshop.tickets.models.mappers;

import com.tickshop.tickets.models.dtos.CreateTicketRequest;
import com.tickshop.tickets.models.dtos.TicketDto;
import com.tickshop.tickets.models.entities.Ticket;

public class TicketMapper {

    public static Ticket dtoToEntity(CreateTicketRequest createTicketRequest) {
        return new Ticket(
                createTicketRequest.showId(),
                createTicketRequest.seatNumber(),
                createTicketRequest.price()
        );
    }

    public static TicketDto entityToDto(Ticket ticket) {
        return new TicketDto(
                ticket.getTicketId(),
                ticket.getBookingId(),
                ticket.getStatus(),
                ticket.getShowId(),
                ticket.getSeatNumber(),
                ticket.getPrice()
        );
    }

}
