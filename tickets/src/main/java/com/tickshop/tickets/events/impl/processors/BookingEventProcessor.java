package com.tickshop.tickets.events.impl.processors;

import com.tickshop.tickets.events.DomainEvent;
import com.tickshop.tickets.events.EventProcessor;
import com.tickshop.tickets.events.booking.events.BookingEvent;
import org.slf4j.Logger;
import reactor.core.publisher.Mono;

public interface BookingEventProcessor<R extends DomainEvent> extends EventProcessor<BookingEvent, R> {

    Logger log = org.slf4j.LoggerFactory.getLogger(BookingEventProcessor.class);

    @Override
    default Mono<R> process(BookingEvent event) {
        return switch (event) {
            case BookingEvent.BookingCreated bookingCreated -> this.handle(bookingCreated);
            case BookingEvent.BookingCancelled bookingCancelled -> this.handle(bookingCancelled);
            case BookingEvent.BookingCompleted bookingCompleted -> this.handle(bookingCompleted);
        };
    }

    Mono<R> handle(BookingEvent.BookingCreated bookingCreated);
    Mono<R> handle(BookingEvent.BookingCancelled ticketReservationFailed);
    Mono<R> handle(BookingEvent.BookingCompleted bookingCompleted);

}
