package com.tickshop.booking.events.impl.eventprocessors;

import com.tickshop.booking.events.DomainEvent;
import com.tickshop.booking.events.EventProcessor;
import com.tickshop.booking.events.impl.events.BookingEvent;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

public interface BookingEventProcessor<R extends DomainEvent> extends EventProcessor<BookingEvent, R> {

    @Override
    default Mono<R> process(BookingEvent event) {
        return switch (event) {
            case BookingEvent.BookingCreated bookingCreated -> this.handle(bookingCreated);
            case BookingEvent.BookingCancelled bookingCancelled -> this.handle(bookingCancelled);
            case BookingEvent.BookingCompleted bookingCompleted -> this.handle(bookingCompleted);
        };
    }

    Mono<R> handle(BookingEvent.BookingCreated bookingCreated);
    Mono<R> handle(BookingEvent.BookingCancelled bookingCancelled);
    Mono<R> handle(BookingEvent.BookingCompleted bookingCompleted);
}
