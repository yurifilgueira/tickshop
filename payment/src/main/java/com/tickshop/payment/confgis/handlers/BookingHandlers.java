package com.tickshop.payment.confgis.handlers;

import com.tickshop.payment.events.booking.BookingEvent;
import com.tickshop.payment.events.impl.events.PaymentEvent;
import com.tickshop.payment.services.PaymentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.function.Function;

@Configuration
public class BookingHandlers {

    private static final Logger log = LoggerFactory.getLogger(BookingHandlers.class);
    private final PaymentService paymentService;
    private StreamBridge streamBridge;

    public BookingHandlers(PaymentService paymentService) {
        this.paymentService = paymentService;
    }


    @Bean
    public Function<Flux<Message<BookingEvent.BookingCancelled>>, Flux<Message<PaymentEvent>>> bookingEventProcessor() {
        log.info("Booking Event Processor");
        return flux -> flux
                .doOnNext(msg -> log.info("Booking Event received {}", msg.getPayload()))
                .map(Message::getPayload)
                .flatMap(this::handle)
                .map(responseEvent -> MessageBuilder.withPayload(responseEvent).build());
    }

    public Mono<PaymentEvent> handle(BookingEvent.BookingCancelled bookingCancelled) {
        return paymentService.refundPayment(bookingCancelled.bookingId())
                .flatMap(refund -> Mono.just((PaymentEvent) new PaymentEvent.PaymentRefunded(
                        bookingCancelled.bookingId(),
                        refund.getBookingId(),
                        refund.getAmount(),
                        Instant.now()
                ))).then(Mono.empty());
    }

}