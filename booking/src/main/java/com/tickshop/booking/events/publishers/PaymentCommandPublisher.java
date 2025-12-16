package com.tickshop.booking.events.publishers;

import com.tickshop.booking.events.commands.PaymentCommand;
import com.tickshop.booking.events.commands.TicketCommand;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import java.time.Duration;

@Component
public class PaymentCommandPublisher {

    private final Sinks.Many<PaymentCommand> generalSink =
            Sinks.many().unicast().onBackpressureBuffer();

    public void publish(PaymentCommand event) {
        generalSink.emitNext(event, Sinks.EmitFailureHandler.busyLooping(Duration.ofSeconds(1)));
    }

    public Flux<PaymentCommand> getGeneralFlux() {
        return generalSink.asFlux();
    }

}