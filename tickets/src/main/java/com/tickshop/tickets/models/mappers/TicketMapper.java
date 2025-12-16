package com.tickshop.tickets.models.mappers;

import com.tickshop.tickets.models.dtos.CreateTicketRequest;
import com.tickshop.tickets.models.dtos.TicketDto;
import com.tickshop.tickets.models.entities.Ticket;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class TicketMapper {

    public static Ticket dtoToEntity(CreateTicketRequest createTicketRequest) {
        return new Ticket(
                createTicketRequest.showId(),
                (long) ThreadLocalRandom.current().nextInt(1, 1000000),
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
