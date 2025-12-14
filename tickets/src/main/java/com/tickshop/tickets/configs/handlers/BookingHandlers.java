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
    private StreamBridge streamBridge;

    public BookingHandlers(TicketService ticketService, StreamBridge streamBridge) {
        this.ticketService = ticketService;
        this.streamBridge = streamBridge;
    }

    @Bean
    public Function<Flux<Message<BookingEvent>>, Flux<Message<TicketEvent>>> bookingEventProcessor() {
        return flux -> flux
                .doOnNext(msg -> log.info("Booking Event received {}", msg.getPayload()))
                .map(Message::getPayload)
                .flatMap(this::process)
                .map(responseEvent -> MessageBuilder.withPayload(responseEvent).build());
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

                    TicketEvent event = new TicketReservationFailed(e.bookingId(), ex.getMessage(), Instant.now());
                    streamBridge.send("paymentConfirmationProcessor-out-0", MessageBuilder.withPayload(event).build());

                    return Mono.empty();
                });
    }

    @Override
    public Mono<TicketEvent> handle(BookingEvent.BookingCancelled bookingCancelled) {
        return ticketService.freeTickets(bookingCancelled.bookingId())
                .map(_ -> new TicketReservationFailed(
                        bookingCancelled.bookingId(),
                        bookingCancelled.reason(),
                        Instant.now()
                )).then(Mono.empty());
    }

    @Override
    public Mono<TicketEvent> handle(BookingEvent.BookingCompleted bookingCompleted) {
        return null;
    }
}