package com.tickshop.payment.events.impl.eventprocessors;

import com.tickshop.payment.events.DomainEvent;
import com.tickshop.payment.events.EventProcessor;
import com.tickshop.payment.events.commands.PaymentCommand;
import reactor.core.publisher.Mono;

public interface PaymentCommandProcessor<R extends DomainEvent> extends EventProcessor<PaymentCommand, R> {

    @Override
    default Mono<R> process(PaymentCommand event) {
        return switch (event) {
            case PaymentCommand.ProcessPaymentCommand paymentProcessed -> this.handle(paymentProcessed);
            case PaymentCommand.RefundPaymentCommand refundPaymentCommand -> this.handle(refundPaymentCommand);
        };
    }

    Mono<R> handle(PaymentCommand.ProcessPaymentCommand paymentProcessed);
    Mono<R> handle(PaymentCommand.RefundPaymentCommand refundPaymentCommand);
}
