package com.tickshop.tickets.configs.handlers;

import com.tickshop.tickets.events.impl.events.TicketEvent;
import com.tickshop.tickets.events.payment.events.PaymentEvent;
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
public class PaymentHandlers {

    private final TicketService ticketService;
    Logger log = LoggerFactory.getLogger(PaymentHandlers.class);

    public PaymentHandlers(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @Bean
    public Function<Flux<Message<PaymentEvent.PaymentProcessed>>, Flux<Message<TicketEvent>>> paymentConfirmationProcessor() {
        return flux -> flux
                .doOnNext(msg -> log.info("Payment confirmed, launching ticket: {}", msg.getPayload().bookingId()))
                .flatMap(this::launchTicket);
    }

    private Flux<Message<TicketEvent>> launchTicket(Message<PaymentEvent.PaymentProcessed> message) {
        PaymentEvent.PaymentProcessed payload = message.getPayload();

        return ticketService.releaseTicket(payload.bookingId())
                .flatMap(ticket -> {
                    TicketEvent launchedEvent = new TicketEvent.TicketSold(
                            payload.bookingId(),
                            payload.paymentId(),
                            Instant.now()
                    );

                    return Mono.just(MessageBuilder.withPayload(launchedEvent)
                            .setHeader(KafkaHeaders.KEY, ((TicketEvent.TicketSold) launchedEvent).bookingId().toString())
                            .build());
                });
    }

}
