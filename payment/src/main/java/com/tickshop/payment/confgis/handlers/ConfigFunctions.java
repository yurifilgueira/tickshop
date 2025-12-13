package com.tickshop.payment.confgis.handlers;

import com.tickshop.payment.events.impl.events.PaymentEvent;
import com.tickshop.payment.events.ticket.TicketEvent;
import com.tickshop.payment.services.PaymentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;
import java.util.function.Function;

@Configuration
public class ConfigFunctions {

    private final PaymentService paymentService;
    private final Logger log = LoggerFactory.getLogger(ConfigFunctions.class);

    public ConfigFunctions(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @Bean
    public Function<Flux<Message<TicketEvent.TicketReserved>>, Flux<Message<PaymentEvent>>> ticketReservedHandler() {
        return flux -> flux
                .doOnNext(msg  -> log.info("Ticket event received: {}", msg.getPayload()))
                .flatMap(this::processMessage);
    }

    private Mono<Message<PaymentEvent>> processMessage(Message<TicketEvent.TicketReserved> message) {
        TicketEvent payload = message.getPayload();

        if (!(payload instanceof TicketEvent.TicketReserved)) {
            return Mono.empty();
        }

        return paymentService.processPayment((TicketEvent.TicketReserved) payload)
                .flatMap(payment -> {
                    return Mono.just((PaymentEvent) new PaymentEvent.PaymentProcessed(
                            payment.getBookingId(),
                            payment.getPaymentId(),
                            payment.getAmount(),
                            payment.getCreatedAt()
                    ));
                }).map(response -> MessageBuilder.withPayload(response)
                        .setHeader(KafkaHeaders.KEY, extractKey(response))
                        .build());
    }

    private String extractKey(PaymentEvent event) {
        if (event instanceof PaymentEvent.PaymentProcessed r) {
            return r.bookingId().toString();
        } else if (event instanceof PaymentEvent.PaymentDeclined f) {
            return f.bookingId().toString();
        }
        return UUID.randomUUID().toString();
    }
}
