package com.tickshop.booking.events;

import java.time.Instant;

public interface DomainEvent {

    Instant createdAt();

}
