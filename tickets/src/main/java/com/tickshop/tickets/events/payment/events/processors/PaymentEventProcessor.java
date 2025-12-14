package com.tickshop.tickets.events.payment.events.processors;

import com.tickshop.tickets.events.DomainEvent;
import com.tickshop.tickets.events.EventProcessor;
import com.tickshop.tickets.events.booking.events.BookingEvent;
import com.tickshop.tickets.events.impl.events.TicketEvent;
import com.tickshop.tickets.events.payment.events.PaymentEvent;
import org.slf4j.Logger;
import org.springframework.messaging.Message;
import reactor.core.publisher.Mono;

public interface PaymentEventProcessor<R extends DomainEvent> extends EventProcessor<PaymentEvent, R> {

    Logger log = org.slf4j.LoggerFactory.getLogger(PaymentEventProcessor.class);

    @Override
    default Mono<R> process(PaymentEvent event) {
        return switch (event) {
            case PaymentEvent.PaymentProcessed paymentProcessed -> this.handle(paymentProcessed);
            case PaymentEvent.PaymentDeclined paymentDeclined -> this.handle(paymentDeclined);
            case PaymentEvent.PaymentRefunded paymentRefunded -> this.handle(paymentRefunded);
        };
    }

    Mono<R> handle(PaymentEvent.PaymentProcessed paymentProcessed);
    Mono<R> handle(PaymentEvent.PaymentDeclined paymentDeclined);
    Mono<R> handle(PaymentEvent.PaymentRefunded paymentRefunded);

}