package com.sebbe.cinema.controllers;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/tickets")
public class TicketController {

    @PostMapping
    public String buyTicket() {
        return "POST /api/v1/tickets";
    }

    @GetMapping
    public String listTickets(@RequestParam Long customerId) {
        return "GET /api/v1/tickets?customerId=" + customerId;
    }

}
