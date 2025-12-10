package com.tickshop.tickets.events;

import java.time.Instant;

public interface DomainEvent {

    Instant createdAt();

}
