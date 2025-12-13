package com.tickshop.booking.events.impl.publishers;

import com.tickshop.booking.events.EventPublisher;
import com.tickshop.booking.events.impl.events.BookingEvent;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

@Component
public class BookingEventPublisher implements EventPublisher<BookingEvent> {

    private final Sinks.Many<BookingEvent> sink = Sinks.many().unicast().onBackpressureBuffer();

    @Override
    public void publish(BookingEvent event) {
        sink.emitNext(event, Sinks.EmitFailureHandler.FAIL_FAST);
    }

    public Flux<BookingEvent> getFlux() {
        return sink.asFlux()
                .ofType(BookingEvent.class);
    }

}