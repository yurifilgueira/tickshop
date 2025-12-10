package com.tickshop.tickets.events.impl;

import com.tickshop.tickets.events.EventPublisher;
import com.tickshop.tickets.events.impl.events.TicketEvent;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

@Component
public class TicketEventPublisher implements EventPublisher<TicketEvent> {

    private final Sinks.Many<TicketEvent> sink = Sinks.many().unicast().onBackpressureBuffer();

    @Override
    public void publish(TicketEvent event) {
        sink.emitNext(event, Sinks.EmitFailureHandler.FAIL_FAST);
    }

    public Flux<TicketEvent> getFlux() {
        return sink.asFlux();
    }

}
