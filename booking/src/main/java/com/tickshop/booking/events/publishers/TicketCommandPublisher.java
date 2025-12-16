package com.tickshop.booking.events.publishers;

import com.tickshop.booking.events.commands.TicketCommand;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import java.time.Duration;

@Component
public class TicketCommandPublisher {

    private final Sinks.Many<TicketCommand> generalSink =
            Sinks.many().unicast().onBackpressureBuffer();

    public void publish(TicketCommand event) {
        generalSink.emitNext(event, Sinks.EmitFailureHandler.busyLooping(Duration.ofSeconds(1)));
    }

    public Flux<TicketCommand> getGeneralFlux() {
        return generalSink.asFlux();
    }

}