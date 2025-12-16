package com.tickshop.booking.events.processors;


import com.tickshop.booking.events.EventProcessor;
import com.tickshop.booking.events.payment.PaymentEvent;
import reactor.core.publisher.Mono;

public interface PaymentEventProcessor<R> extends EventProcessor<PaymentEvent, R> {

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
