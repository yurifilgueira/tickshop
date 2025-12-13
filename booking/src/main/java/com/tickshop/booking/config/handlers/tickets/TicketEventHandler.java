package com.tickshop.booking.config.handlers.tickets;

import com.tickshop.booking.events.tickets.TicketEvent;
import com.tickshop.booking.services.BookingService;
import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.function.Consumer;

@Configuration
public class TicketEventHandler {

    private final BookingService bookingService;
    private Logger log = LoggerFactory.getLogger(TicketEventHandler.class);

    public TicketEventHandler(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @Bean
    public Consumer<Flux<Message<TicketEvent.TicketSold>>> soldTicketEventHandler() {
        log.info("############## TicketEventHandler running");
        return flux -> flux
                .doOnNext(msg -> log.info("Sold ticket event received: {}", msg.getPayload()))
                .flatMap(this::processMessage)
                .subscribe();
    }

    private Mono<Void> processMessage(Message<TicketEvent.TicketSold> ticketSoldMessage) {
        TicketEvent.TicketSold ticketSoldEvent = ticketSoldMessage.getPayload();

        return  bookingService.confirmBooking(ticketSoldEvent.bookingId());
    }

}
