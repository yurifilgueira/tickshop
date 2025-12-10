package com.tickshop.tickets.models.dtos;

import com.tickshop.tickets.models.enums.TicketStatus;

import java.math.BigDecimal;
import java.util.UUID;

public record TicketDto(
        UUID ticketId,
        UUID bookingId,
        TicketStatus status,
        UUID showId,
        Long seatNumber,
        BigDecimal price
) {
}