package com.tickshop.tickets.configs.handlers;

import com.tickshop.tickets.events.booking.events.BookingEvent;
import com.tickshop.tickets.events.impl.processors.BookingEventProcessor;
import com.tickshop.tickets.events.impl.events.TicketEvent;
import com.tickshop.tickets.events.impl.events.TicketEvent.TicketReservationFailed;
import com.tickshop.tickets.events.impl.events.TicketEvent.TicketReserved;
import com.tickshop.tickets.services.TicketService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.function.Function;

@Configuration
public class BookingHandlers implements BookingEventProcessor<TicketEvent> {

    private static final Logger log = LoggerFactory.getLogger(BookingHandlers.class);
    private final TicketService ticketService;
    // Removido StreamBridge: não precisamos dele aqui

    public BookingHandlers(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @Bean
    public Function<Flux<Message<BookingEvent>>, Flux<Message<TicketEvent>>> bookingEventProcessor() {
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
    public Mono<TicketEvent> handle(BookingEvent.BookingCreated e) {
        return ticketService.reserveTickets(e.bookingId(), e.showId(), e.ticketsQtt())
                .collectList()
                .flatMap(tickets -> {
                    if (tickets.isEmpty()) {
                        return Mono.error(new RuntimeException("No tickets reserved"));
                    }
                    return Mono.just((TicketEvent) new TicketReserved(
                            e.bookingId(),
                            tickets.get(0).ticketId(),
                            e.customerId(),
                            e.price(),
                            Instant.now()
                    ));
                })
                .onErrorResume(ex -> {
                    log.warn("Error reserving tickets for booking {}: {}", e.bookingId(), ex.getMessage());
                    return Mono.just(new TicketReservationFailed(
                            e.bookingId(),
                            ex.getMessage(),
                            Instant.now()
                    ));
                });
    }

    @Override
    public Mono<TicketEvent> handle(BookingEvent.BookingCancelled bookingCancelled) {
        return ticketService.freeTickets(bookingCancelled.bookingId())
                .then(Mono.empty());
    }
    @Override
    public Mono<TicketEvent> handle(BookingEvent.BookingCompleted bookingCompleted) {
        return null;
    }
}