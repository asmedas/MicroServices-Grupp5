package com.sebbe.cinema.controllers;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/bookings")
public class BookingController {

    @PostMapping
    public String createBooking() {
        return "POST /api/v1/bookings";
    }

    @PatchMapping("/{bookingId}")
    public String updateBooking(@PathVariable Long bookingId) {
        return "PATCH /api/v1/bookings/" + bookingId;
    }

    @GetMapping
    public String listBookingsByCustomer(@RequestParam Long customerId) {
        return "GET /api/v1/bookings?customerId=" + customerId;
    }

}
