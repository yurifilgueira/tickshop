package com.tickshop.booking.config.handlers;

import com.tickshop.booking.events.processors.TicketEventProcessor;
import com.tickshop.booking.events.tickets.TicketEvent;
import com.tickshop.booking.services.BookingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.function.Consumer;

@Configuration
public class TicketEventHandler implements TicketEventProcessor<Void> {

    private final BookingService bookingService;
    private final Logger log = LoggerFactory.getLogger(TicketEventHandler.class);

    public TicketEventHandler(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @Bean
    public Consumer<Flux<Message<TicketEvent>>> ticketEventProcessor() {
        return flux -> flux
                .doOnNext(msg -> log.info("Ticket event received: {}", msg.getPayload()))
                .map(Message::getPayload)
                .flatMap(this::process)
                .subscribe();
    }

    @Override
    public Mono<Void> handle(TicketEvent.TicketReserved ticketReserved) {
        return bookingService.processPayment(ticketReserved);
    }

    @Override
    public Mono<Void> handle(TicketEvent.TicketReservationFailed ticketReservationFailed) {
        return bookingService.cancelBooking(ticketReservationFailed.bookingId());
    }

    @Override
    public Mono<Void> handle(TicketEvent.TicketSold ticketSold) {
        return  bookingService.confirmBooking(ticketSold.bookingId());
    }
}
