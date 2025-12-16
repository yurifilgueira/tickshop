package com.tickshop.tickets.configs.handlers;

import com.tickshop.tickets.events.booking.events.TicketCommand;
import com.tickshop.tickets.events.impl.processors.CommandProcessor;
import com.tickshop.tickets.events.impl.events.TicketEvent;
import com.tickshop.tickets.events.impl.events.TicketEvent.TicketReservationFailed;
import com.tickshop.tickets.events.impl.events.TicketEvent.TicketReserved;
import com.tickshop.tickets.services.TicketService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.function.Function;

@Configuration
public class CommandHandlers implements CommandProcessor<TicketEvent> {

    private static final Logger log = LoggerFactory.getLogger(CommandHandlers.class);
    private final TicketService ticketService;

    public CommandHandlers(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @Bean
    public Function<Flux<Message<TicketCommand>>, Flux<Message<TicketEvent>>> ticketCommandProcessor() {
        return flux -> flux
                .doOnNext(msg -> log.info("Booking Event received {}", msg.getPayload()))
                .map(Message::getPayload)
                .flatMap(this::process)
                .map(event -> {
                    String routingKey;
                    if (event instanceof TicketReserved) {
                        routingKey = "ticket.reserved";
                    } else if (event instanceof TicketReservationFailed) {
                        routingKey = "ticket.failed";
                    } else {
                        routingKey = "ticket.audit";
                    }
                    return MessageBuilder.withPayload(event)
                            .setHeader("routingKey", routingKey)
                            .build();
                });
    }

    @Override
    public Mono<TicketEvent> handle(TicketCommand.ReserveTicketCommand e) {
        return ticketService.reserveTickets(e.bookingId(), e.showId(), e.quantity())
                .collectList()
                .flatMap(tickets -> {
                    if (tickets.isEmpty()) {
                        return Mono.error(new RuntimeException("No tickets reserved"));
                    }
                    return Mono.just((TicketEvent) new TicketReserved(
                            e.bookingId(),
                            tickets.get(0).ticketId(),
                            e.customerId(),
                            e.amount(),
                            Instant.now()
                    ));
                })
                .onErrorResume(ex -> {
                    log.warn("Error reserving tickets for booking {}: {}", e.bookingId(), ex.getMessage());
                    TicketEvent event = new TicketReservationFailed(e.bookingId(), ex.getMessage(), Instant.now());
                    return Mono.just(event);
                });
    }

    @Override
    public Mono<TicketEvent> handle(TicketCommand.SellTicketCommand command) {
        return ticketService.launchTicket(command.bookingId())
                .doOnNext(tickets -> log.info("Payment confirmed, launching ticket: {}", tickets))
                .flatMap(soldTickets -> {
                    if (soldTickets.isEmpty()) {
                        String errorMsg = "Tickets not found/reserved for booking " + command.bookingId();
                        log.warn(errorMsg);
                        return Mono.error(new RuntimeException(errorMsg));
                    }
                    return Mono.just((TicketEvent) new TicketEvent.TicketSold(
                            command.bookingId()
                    ));
                })
                .onErrorResume(ex -> {
                    log.error("Error selling tickets: {}", ex.getMessage());
                    return Mono.just(new TicketEvent.TicketReservationFailed(
                            command.bookingId(),
                            "SELL_FAILED: " + ex.getMessage(),
                            Instant.now()
                    ));
                });
    }

    @Override
    public Mono<TicketEvent> handle(TicketCommand.CancelReservationCommand bookingCancelled) {
        return ticketService.freeTickets(bookingCancelled.bookingId())
                .then(Mono.empty());
    }

}