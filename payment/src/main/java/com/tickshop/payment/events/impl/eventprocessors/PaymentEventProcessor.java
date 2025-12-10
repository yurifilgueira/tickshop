package com.tickshop.payment.events.impl.eventprocessors;

import com.tickshop.payment.events.DomainEvent;
import com.tickshop.payment.events.EventProcessor;
import com.tickshop.payment.events.impl.events.PaymentEvent;
import reactor.core.publisher.Mono;

public interface PaymentEventProcessor <R extends DomainEvent> extends EventProcessor<PaymentEvent, R> {

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
