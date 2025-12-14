package com.tickshop.tickets.configs.handlers;

import com.tickshop.tickets.events.impl.events.TicketEvent;
import com.tickshop.tickets.events.payment.events.PaymentEvent;
import com.tickshop.tickets.events.payment.events.processors.PaymentEventProcessor;
import com.tickshop.tickets.services.TicketService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.function.Function;

@Configuration
public class PaymentHandlers implements PaymentEventProcessor<TicketEvent> {

    private final TicketService ticketService;
    Logger log = LoggerFactory.getLogger(PaymentHandlers.class);

    public PaymentHandlers(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @Bean
    public Function<Flux<Message<PaymentEvent>>, Flux<Message<TicketEvent>>> paymentConfirmationProcessor() {
        return flux -> flux
                .map(Message::getPayload)
                .flatMap(this::process)
                .map(responseEvent -> MessageBuilder.withPayload(responseEvent).build());
    }

    @Override
    public Mono<TicketEvent> handle(PaymentEvent.PaymentProcessed paymentProcessed) {
        return ticketService.launchTicket(paymentProcessed.bookingId())
                .doOnNext(tickets -> log.info("Payment confirmed, launching ticket: {}", tickets))
                .flatMap(soldTickets -> {
                    if (soldTickets.isEmpty()) {
                        log.warn("No tickets found {}", paymentProcessed.bookingId());
                        return Mono.empty();
                    }
                    return Mono.just((TicketEvent) new TicketEvent.TicketSold(
                            paymentProcessed.bookingId(),
                            paymentProcessed.customerId(),
                            paymentProcessed.paymentId(),
                            Instant.now()
                    ));
                });
    }

    @Override
    public Mono<TicketEvent> handle(PaymentEvent.PaymentDeclined paymentDeclined) {
        return ticketService.freeTickets(paymentDeclined.bookingId())
                .doOnNext(tickets -> log.info("Payment declined, freeing tickets..."))
                .map(_ -> (TicketEvent) new TicketEvent.TicketReservationFailed(
                        paymentDeclined.bookingId(),
                        paymentDeclined.reason(),
                        Instant.now()
                )).switchIfEmpty(Mono.defer(Mono::empty));
    }

    @Override
    public Mono<TicketEvent> handle(PaymentEvent.PaymentRefunded paymentRefunded) {
        return null;
    }
}
