package com.miriam.travel.controller;

import com.miriam.travel.dto.booking.BookingPatchRequest;
import com.miriam.travel.dto.booking.BookingRequest;
import com.miriam.travel.dto.booking.BookingResponse;
import com.miriam.travel.service.BookingService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/bookings")
public class BookingController {

    private static final Logger log = LoggerFactory.getLogger(BookingController.class);
    private final BookingService service;

    public BookingController(BookingService service) {
        this.service = service;
    }

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<BookingResponse> create(@Valid @RequestBody BookingRequest req,
                                                  UriComponentsBuilder uriBuilder) {
        BookingResponse saved = service.create(req);
        URI location = uriBuilder.path("/api/v1/bookings/{id}")
                .buildAndExpand(saved.id()).toUri();
        String user = getCurrentUser();
        log.info("{} created a booking id={} for destination={} ({}) hotel={}",
                user, saved.id(), saved.destinationCity(), saved.destinationCountry(), saved.hotelName());

        return ResponseEntity.created(location).body(saved);
    }

    @PatchMapping("/{bookingId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<BookingResponse> patch(@PathVariable Long bookingId,
                                                 @Valid @RequestBody BookingPatchRequest req) {
        BookingResponse updated = service.patch(bookingId, req);
        String user = getCurrentUser();
        log.info("{} updated booking id={} (destination={} ({}) hotel={} weeks={})",
                user, bookingId, updated.destinationCity(), updated.destinationCountry(), updated.hotelName(), updated.weeks());
        return ResponseEntity.ok(updated);
    }

    @GetMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<BookingResponse>> listByCustomer(@RequestParam String customerId) {
        String user = getCurrentUser();
        log.debug("{} requested booking list for customerId={}", user, customerId);
        return ResponseEntity.ok(service.listByCustomer(customerId));
    }


    private String getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return (auth != null) ? auth.getName() : "anonymous";
    }
}