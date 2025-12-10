package com.tickshop.payment.events;

import java.time.Instant;

public interface DomainEvent {

    Instant createdAt();

}
