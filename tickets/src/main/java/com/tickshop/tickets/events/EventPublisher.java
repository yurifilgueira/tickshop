package com.tickshop.tickets.events;

public interface EventPublisher<T extends DomainEvent> {
    void publish(T event);
}
