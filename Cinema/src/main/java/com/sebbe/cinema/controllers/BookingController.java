package com.sebbe.cinema.controllers;

import com.sebbe.cinema.dtos.booking.CreateBookingDto;
import com.sebbe.cinema.dtos.booking.PatchBookingDto;
import com.sebbe.cinema.entities.Booking;
import com.sebbe.cinema.services.BookingService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/bookings")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }


    /**
     * {
     *   "date": "2025-11-15",
     *   "cinemaHallId": 3,
     *   "filmId": 2,
     *   "speaker": "Dr. Jane Doe",
     *   "technicalEquipment": ["PROJECTOR", "AUDIO", "MIC"]
     * }
     */
    @PostMapping
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<Booking> createBooking(@RequestBody @Valid CreateBookingDto bookingRequest) {
        return ResponseEntity.created(URI.create("/api/v1/bookings")).body(bookingService.createBooking(bookingRequest));
    }

    /**
     * {
     *     "date": "2025-11-15",
     *     "technicalEquipment": ["Projector", "Audio", "Mic"]
     * }
     */
    @PatchMapping("/{bookingId}")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<Booking> updateBooking(@PathVariable Long bookingId,
                                @RequestBody @Valid PatchBookingDto dto) {
        return ResponseEntity.ok(bookingService.patchBookingById(bookingId, dto));
    }

    // släpps endast igenom om customerId stämmer överens med den nuvarande användaren
    @GetMapping(params = "customerId")
    @PreAuthorize("hasRole('ROLE_USER')")
    public List<Booking> listBookingsByCustomer(@RequestParam Long customerId) {
        return bookingService.getBookingsByCustomerId(customerId);
    }

}
