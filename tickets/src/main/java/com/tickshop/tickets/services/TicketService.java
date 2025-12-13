package com.tickshop.tickets.services;

import com.tickshop.tickets.models.dtos.CreateTicketRequest;
import com.tickshop.tickets.models.dtos.TicketDto;
import com.tickshop.tickets.models.entities.Ticket;
import com.tickshop.tickets.models.mappers.TicketMapper;
import com.tickshop.tickets.repositories.TicketRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
public class TicketService {

    private final Logger log = LoggerFactory.getLogger(TicketService.class);
    private final TicketRepository ticketRepository;

    public TicketService(TicketRepository ticketRepository) {
        this.ticketRepository = ticketRepository;
    }

    public Mono<Ticket> createTicket(CreateTicketRequest request) {
        var ticket = TicketMapper.dtoToEntity(request);
        return ticketRepository.save(ticket);
    }

    @Transactional
    public Flux<TicketDto> reserveTickets(UUID bookingId, UUID showId, Integer quantity) {
        log.debug("########################### Iniciando tickets reserve");

        return ticketRepository.findAvailableTickets(showId, quantity)
                .collectList()
                .flatMapMany(availableTickets -> {

                    if (availableTickets.size() < quantity) {
                        return Flux.error(new RuntimeException(
                                "Estoque insuficiente. Solicitado: " + quantity +
                                        ", Disponível: " + availableTickets.size()
                        ));
                    }

                    availableTickets.forEach(ticket -> ticket.reserve(bookingId));
                    return ticketRepository.saveAll(availableTickets)
                            .map(TicketMapper::entityToDto);
                });
    }

    public Flux<TicketDto> releaseTicket(UUID uuid) {
        return ticketRepository.findByBookingId(uuid)
                .flatMap(ticket -> {

                    log.info("Ticket################ {} liberado", ticket.getTicketId());
                    ticket.sell();
                    return ticketRepository.save(ticket)
                            .map(TicketMapper::entityToDto);
                });
    }
}
