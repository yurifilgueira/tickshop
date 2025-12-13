package com.tickshop.booking.config;

import com.tickshop.booking.events.impl.events.BookingEvent;
import com.tickshop.booking.events.impl.publishers.BookingEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Flux;

import java.util.function.Supplier;

@Configuration
public class BookingStreamsConfig {

    @Bean
    public Supplier<Flux<BookingEvent>> bookingProducer(BookingEventPublisher publisher) {
        return publisher::getFlux;
    }
}