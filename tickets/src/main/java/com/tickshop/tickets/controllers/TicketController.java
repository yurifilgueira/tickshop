package com.tickshop.tickets.controllers;

import com.tickshop.tickets.models.dtos.CreateTicketRequest;
import com.tickshop.tickets.services.TicketService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(value = "/tickets")
public class TicketController {

    private final TicketService  ticketService;

    public TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @PostMapping
    public Mono<ResponseEntity<?>> save(@RequestBody CreateTicketRequest request) {
        return ticketService.createTicket(request)
                .map(response -> ResponseEntity.status(HttpStatus.CREATED).body(response));
    }
}
