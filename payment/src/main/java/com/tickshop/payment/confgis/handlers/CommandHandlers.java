package com.tickshop.payment.confgis.handlers;

import com.tickshop.payment.events.commands.PaymentCommand;
import com.tickshop.payment.events.impl.eventprocessors.PaymentCommandProcessor;
import com.tickshop.payment.events.impl.events.PaymentEvent;
import com.tickshop.payment.events.ticket.TicketEvent;
import com.tickshop.payment.model.enums.PaymentStatus;
import com.tickshop.payment.services.PaymentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.function.Function;

@Configuration
public class CommandHandlers implements PaymentCommandProcessor<PaymentEvent> {

    private static final Logger log = LoggerFactory.getLogger(CommandHandlers.class);
    private final PaymentService paymentService;
    private final static String PAYMENT_DECLINED = "PAYMENT DECLINED";

    public CommandHandlers(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @Bean
    public Function<Flux<Message<PaymentCommand>>, Flux<Message<PaymentEvent>>> paymentCommandProcessor() {
        return flux -> flux
                .doOnNext(msg  -> log.info("Booking event received: {}", msg.getPayload()))
                .flatMap(msg -> this.process(msg.getPayload()))
                .map(responseEvent -> MessageBuilder.withPayload(responseEvent).build());
    }


    @Override
    public Mono<PaymentEvent> handle(PaymentCommand.ProcessPaymentCommand paymentProcessed) {
        return paymentService.processPayment(paymentProcessed)
                .flatMap(payment -> {
                    if (payment.getStatus() == PaymentStatus.DECLINED) {
                        log.warn("Payment refused: {}", payment.getPaymentId());
                        return Mono.just((PaymentEvent) new PaymentEvent.PaymentDeclined(
                                payment.getBookingId(),
                                PAYMENT_DECLINED,
                                Instant.now()
                        ));
                    }
                    return Mono.just((PaymentEvent) new PaymentEvent.PaymentProcessed(
                            payment.getBookingId(),
                            payment.getPaymentId(),
                            payment.getAmount(),
                            payment.getCreatedAt()
                    ));
                })
                .onErrorResume(e -> {
                    log.error("Payment error: {}", e.getMessage());
                    return Mono.just((PaymentEvent) new PaymentEvent.PaymentDeclined(
                            paymentProcessed.bookingId(),
                            e.getMessage(),
                            Instant.now()
                    ));
                });
    }

    @Override
    public Mono<PaymentEvent> handle(PaymentCommand.RefundPaymentCommand refundPaymentCommand) {
        return paymentService.refundPayment(refundPaymentCommand.bookingId())
                .flatMap(refund -> Mono.just((PaymentEvent) new PaymentEvent.PaymentRefunded(
                        refundPaymentCommand.bookingId(),
                        refund.getBookingId(),
                        refund.getAmount(),
                        Instant.now()
                )));
    }

}
