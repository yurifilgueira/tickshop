package com.tickshop.tickets.repositories;

import com.tickshop.tickets.events.impl.events.TicketEvent;
import com.tickshop.tickets.models.entities.Ticket;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.util.UUID;

@Repository
public interface TicketRepository extends R2dbcRepository<Ticket, UUID> {
    @Query("SELECT * FROM tickets WHERE show_id = :showId AND status = 'AVAILABLE' LIMIT :limit")
    Flux<Ticket> findAvailableTickets(UUID showId, Integer limit);
    Flux<Ticket> findByBookingId(UUID bookingId);
}
