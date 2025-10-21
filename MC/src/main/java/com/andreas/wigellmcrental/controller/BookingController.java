package com.andreas.wigellmcrental.controller;

import com.andreas.wigellmcrental.dto.BookingCreateDto;
import com.andreas.wigellmcrental.dto.BookingDto;
import com.andreas.wigellmcrental.dto.BookingUpdateDto;
import com.andreas.wigellmcrental.entity.Booking;
import com.andreas.wigellmcrental.entity.BookingStatus;
import com.andreas.wigellmcrental.mapper.Mapper;
import com.andreas.wigellmcrental.service.BookingService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class BookingController {

    private final BookingService service;

    public BookingController(BookingService service) {
        this.service = service;
    }


    // Admin: lista alla bokningar
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/bookings")
    public List<BookingDto> all() {
        return service.all().stream().map(Mapper::toBookingDto).toList();
    }


    // Kund/Admin: lista bokningar för en kund (via query-param, enligt uppgift)
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @GetMapping(value = "/bookings", params = "customerId")
    public List<BookingDto> getBookingsByCustomer(@RequestParam Long customerId, Authentication auth) {
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        // Om användaren inte är admin -> får bara se sina egna bokningar
        if (!isAdmin) {
            String username = auth.getPrincipal() instanceof org.springframework.security.oauth2.jwt.Jwt jwt
                    ? jwt.getClaimAsString("preferred_username")
                    : auth.getName();
            Long userCustomerId = service.getCustomerIdByUsername(username);
            if (!userCustomerId.equals(customerId)) {
                throw new RuntimeException("Du får bara se dina egna bokningar");
            }
        }

        return service.byCustomer(customerId).stream()
                .map(Mapper::toBookingDto)
                .toList();
    }


    // Kund/Admin: lista bokningar för kund
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @GetMapping("/bookings/{id}")
    public BookingDto getBooking(@PathVariable Long id) {
        return Mapper.toBookingDto(service.getBookingById(id));
    }


    // Kund/Admin: skapa bokning
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @PostMapping("/bookings")
    public BookingDto create(@Valid @RequestBody BookingCreateDto in) {
        Booking saved = service.create(in.customerId(), in.bikeId(), in.startDate(), in.endDate());
        return Mapper.toBookingDto(saved);
    }

    // Admin: ersätt bokning (PUT)
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/bookings/{id}")
    public BookingDto replace(@PathVariable Long id, @Valid @RequestBody BookingCreateDto in) {
        Booking updated = service.replace(id, in.customerId(), in.bikeId(), in.startDate(), in.endDate());
        return Mapper.toBookingDto(updated);
    }

    // Kund/Admin: patch (kund får inte ändra status – det avskiljs i service via isAdmin-flaggan)
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @PatchMapping("/bookings/{id}")
    public BookingDto patch(@PathVariable Long id, @RequestBody BookingUpdateDto in, Authentication auth) {
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        Booking updated = service.patch(id, in.bikeId(), in.startDate(), in.endDate(), in.status(), isAdmin);
        return Mapper.toBookingDto(updated);
    }

    // Admin: snabb endpoint för status
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/bookings/{id}/status")
    public BookingDto setStatus(@PathVariable Long id, @RequestParam BookingStatus status) {
        return Mapper.toBookingDto(service.setStatus(id, status));
    }

    // Admin: ta bort
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/bookings/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
