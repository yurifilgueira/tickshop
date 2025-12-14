package com.tickshop.booking.events.impl.publishers;

import com.tickshop.booking.events.EventPublisher;
import com.tickshop.booking.events.impl.events.BookingEvent;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import java.time.Duration;

@Component
public class BookingEventPublisher implements EventPublisher<BookingEvent> {

    private final Sinks.Many<BookingEvent> generalSink =
            Sinks.many().unicast().onBackpressureBuffer();

    private final Sinks.Many<BookingEvent> cancellationSink =
            Sinks.many().unicast().onBackpressureBuffer();

    @Override
    public void publish(BookingEvent event) {
        if (event instanceof BookingEvent.BookingCancelled) {
            cancellationSink.emitNext(event, Sinks.EmitFailureHandler.busyLooping(Duration.ofSeconds(1)));
        } else {
            generalSink.emitNext(event, Sinks.EmitFailureHandler.busyLooping(Duration.ofSeconds(1)));
        }
    }

    public Flux<BookingEvent> getGeneralFlux() {
        return generalSink.asFlux();
    }

    public Flux<BookingEvent.BookingCancelled> getCancellationFlux() {
        return cancellationSink.asFlux().ofType(BookingEvent.BookingCancelled.class);
    }
}