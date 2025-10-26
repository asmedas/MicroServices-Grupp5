package com.sebbe.cinema.controllers;

import com.sebbe.cinema.dtos.ticketDtos.CreateTicketDto;
import com.sebbe.cinema.entities.Ticket;
import com.sebbe.cinema.services.TicketService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/tickets")
public class TicketController {

    private final TicketService ticketService;

    public TicketController(TicketService ticketService){
        this.ticketService = ticketService;
    }

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Ticket> buyTicket(@RequestBody @Valid CreateTicketDto dto) {
        return ResponseEntity.created(URI.create("api/v1/tickets")).body(ticketService.createTicket(dto.screeningId()));
    }

    @GetMapping(params = "customerId")
    @PreAuthorize("hasRole('USER') and @ownership.isSelf(authentication, #customerId)")
    public List<Ticket> listTickets(@RequestParam Long customerId) {
        return ticketService.getTicketsByCustomerId(customerId);
    }

}
