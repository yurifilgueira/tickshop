package com.tickshop.payment.events;

import reactor.core.publisher.Mono;

public interface EventProcessor<T, R> {
    Mono<R> process(T event);
}
