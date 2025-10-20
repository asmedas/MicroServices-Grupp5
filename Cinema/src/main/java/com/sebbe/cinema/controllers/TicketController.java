package com.sebbe.cinema.controllers;

import com.sebbe.cinema.entities.Ticket;
import com.sebbe.cinema.services.TicketService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/tickets")
public class TicketController {

    private final TicketService ticketService;

    public TicketController(TicketService ticketService){
        this.ticketService = ticketService;
    }

    @PostMapping
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<Ticket> buyTicket(@RequestParam Long screenindId) {
        return ResponseEntity.ok(ticketService.createTicket(screenindId));
    }

    @GetMapping
    @PreAuthorize("hasRole('ROLE_USER') and @ownership(authentication, customerId)")
    public List<Ticket> listTickets(@RequestParam Long customerId) {
        return ticketService.getTicketsByCustomerId(customerId);
    }

}
