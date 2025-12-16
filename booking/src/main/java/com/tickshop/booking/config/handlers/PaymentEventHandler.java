package com.tickshop.booking.config.handlers;

import com.tickshop.booking.events.processors.PaymentEventProcessor;
import com.tickshop.booking.services.BookingService;
import com.tickshop.booking.events.payment.PaymentEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.function.Consumer;

@Configuration
public class PaymentEventHandler implements PaymentEventProcessor<Void> {

    private final BookingService bookingService;
    private final Logger log = LoggerFactory.getLogger(PaymentEventHandler.class);

    public PaymentEventHandler(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @Bean
    public Consumer<Flux<Message<PaymentEvent>>> paymentEventProcessor() {
        return flux -> flux
                .doOnNext(msg -> log.info("Payment event received: {}", msg.getPayload()))
                .map(Message::getPayload)
                .flatMap(this::process)
                .subscribe();
    }

    @Override
    public Mono<Void> handle(PaymentEvent.PaymentProcessed paymentProcessed) {
        return bookingService.sellTicket(paymentProcessed.bookingId());
    }

    @Override
    public Mono<Void> handle(PaymentEvent.PaymentDeclined paymentDeclined) {
        return bookingService.cancelBookingAndPublishCancelReservation(paymentDeclined.bookingId()).then();
    }

    @Override
    public Mono<Void> handle(PaymentEvent.PaymentRefunded paymentRefunded) {
        return bookingService.cancelBookingAndPublishCancelReservation(paymentRefunded.bookingId()).then();
    }
}
