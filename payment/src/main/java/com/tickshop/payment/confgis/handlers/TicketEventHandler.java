package com.tickshop.payment.confgis.handlers;

import com.tickshop.payment.events.impl.eventprocessors.TicketEventProcessor;
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

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;
import java.util.function.Function;

import static java.lang.Thread.sleep;

@Configuration
public class TicketEventHandler implements TicketEventProcessor<PaymentEvent> {

    private final PaymentService paymentService;
    private final Logger log = LoggerFactory.getLogger(TicketEventHandler.class);
    private final static String PAYMENT_DECLINED = "PAYMENT DECLINED";

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
                .flatMap(payment -> {
                    try {
                        sleep(Duration.ofSeconds(5));
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
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
                            ticketReserved.bookingId(),
                            e.getMessage(),
                            Instant.now()
                    ));
                });
    }

    @Override
    public Mono<PaymentEvent> handle(TicketEvent.TicketReservationFailed ticketReservationFailed) {
        return paymentService.refundPayment(ticketReservationFailed.bookingId())
                .flatMap(refund -> Mono.just((PaymentEvent) new PaymentEvent.PaymentRefunded(
                        ticketReservationFailed.bookingId(),
                        refund.getBookingId(),
                        refund.getAmount(),
                        Instant.now()
                )));
    }

    @Override
    public Mono<PaymentEvent> handle(TicketEvent.TicketSold ticketSold) {
        return null;
    }
}
