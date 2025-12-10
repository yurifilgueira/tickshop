package com.tickshop.booking.events;

import com.tickshop.booking.events.impl.events.BookingEvent;
import reactor.core.publisher.Flux;

public interface EventPublisher <T extends DomainEvent> {
    void publish(BookingEvent event);
}
