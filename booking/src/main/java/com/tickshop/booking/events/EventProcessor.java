package com.tickshop.booking.events;

import reactor.core.publisher.Mono;

public interface EventProcessor<T extends DomainEvent, R> {
    Mono<R> process(T event);
}
