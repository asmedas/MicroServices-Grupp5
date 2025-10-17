package com.sebbe.cinema.controllers;

import com.sebbe.cinema.dtos.booking.CreateBookingDto;
import com.sebbe.cinema.entities.Booking;
import com.sebbe.cinema.services.BookingService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/bookings")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @PostMapping
    public ResponseEntity<Booking> createBooking(@RequestBody @Valid CreateBookingDto bookingRequest) {
        return ResponseEntity.ok(bookingService.createBooking(bookingRequest));
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
