package com.tickshop.payment.confgis.handlers;

import com.tickshop.payment.events.impl.eventprocessors.TicketEventProcessor;
import com.tickshop.payment.events.impl.events.PaymentEvent;
import com.tickshop.payment.events.ticket.TicketEvent;
import com.tickshop.payment.services.PaymentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;
import java.util.function.Function;

@Configuration
public class TicketEventHandler implements TicketEventProcessor<PaymentEvent> {

    private final PaymentService paymentService;
    private final Logger log = LoggerFactory.getLogger(TicketEventHandler.class);

    public TicketEventHandler(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @Bean
    public Function<Flux<Message<TicketEvent>>, Flux<Message<PaymentEvent>>> ticketReservedHandler() {
        return flux -> flux
                .doOnNext(msg  -> log.info("Ticket event received: {}", msg.getPayload()))
                .flatMap(msg -> this.process(msg.getPayload()))
                .map(responseEvent -> MessageBuilder.withPayload(responseEvent).build());
    }

    @Override
    public Mono<PaymentEvent> handle(TicketEvent.TicketReserved ticketReserved) {
        return paymentService.processPayment(ticketReserved)
                .flatMap(payment -> Mono.just((PaymentEvent) new PaymentEvent.PaymentProcessed(
                        payment.getBookingId(),
                        payment.getPaymentId(),
                        payment.getAmount(),
                        payment.getCreatedAt()
                )));
    }

    @Override
    public Mono<PaymentEvent> handle(TicketEvent.TicketReservationFailed ticketReservationFailed) {
        return null;
    }

    @Override
    public Mono<PaymentEvent> handle(TicketEvent.TicketSold ticketSold) {
        return null;
    }
}
