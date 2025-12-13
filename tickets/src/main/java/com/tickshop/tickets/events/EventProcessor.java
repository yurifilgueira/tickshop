package com.tickshop.tickets.events;

import org.springframework.messaging.Message;
import reactor.core.publisher.Mono;

public interface EventProcessor <T extends DomainEvent, R extends DomainEvent> {
    Mono<R> process(T event);
}
