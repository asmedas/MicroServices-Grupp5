package com.andreas.wigellmcrental.controller;

import com.andreas.wigellmcrental.dto.BookingCreateDto;
import com.andreas.wigellmcrental.dto.BookingDto;
import com.andreas.wigellmcrental.dto.BookingUpdateDto;
import com.andreas.wigellmcrental.entity.Booking;
import com.andreas.wigellmcrental.entity.BookingStatus;
import com.andreas.wigellmcrental.mapper.Mapper;
import com.andreas.wigellmcrental.service.BookingService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
public class BookingController {

    private final BookingService service;

    public BookingController(BookingService service) {
        this.service = service;
    }

    // ADMIN: lista alla bokningar
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/bookings")
    public List<BookingDto> all() {
        return service.all().stream().map(Mapper::toBookingDto).toList();
    }

    // USER/ADMIN: lista bokningar för en kund (via query-param)
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @GetMapping(value = "/bookings", params = "customerId")
    public List<BookingDto> getBookingsByCustomer(@RequestParam Long customerId, Authentication auth) {
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

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

    // USER/ADMIN: hämta specifik bokning
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @GetMapping("/bookings/{id}")
    public BookingDto getBooking(@PathVariable Long id) {
        return Mapper.toBookingDto(service.getBookingById(id));
    }

    // USER/ADMIN: skapa bokning
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @PostMapping("/bookings")
    public ResponseEntity<Map<String, Object>> create(@Valid @RequestBody BookingCreateDto in) {
        Booking saved = service.create(in.customerId(), in.bikeId(), in.startDate(), in.endDate());
        return message("Booking created successfully",
                Mapper.toBookingDto(saved),
                HttpStatus.CREATED);
    }

    // ADMIN: ersätt bokning (PUT)
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/bookings/{id}")
    public ResponseEntity<Map<String, Object>> replace(@PathVariable Long id,
                                                       @Valid @RequestBody BookingCreateDto in) {
        Booking updated = service.replace(id, in.customerId(), in.bikeId(), in.startDate(), in.endDate());
        return message("Booking " + id + " replaced successfully",
                Mapper.toBookingDto(updated),
                HttpStatus.OK);
    }

    // USER/ADMIN: patch (kund får inte ändra status)
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @PatchMapping("/bookings/{id}")
    public ResponseEntity<Map<String, Object>> patch(@PathVariable Long id,
                                                     @RequestBody BookingUpdateDto in,
                                                     Authentication auth) {
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        Booking updated = service.patch(id, in.bikeId(), in.startDate(), in.endDate(), in.status(), isAdmin);
        return message("Booking " + id + " updated successfully",
                Mapper.toBookingDto(updated),
                HttpStatus.OK);
    }

    // ADMIN: snabb endpoint för att ändra status
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/bookings/{id}/status")
    public ResponseEntity<Map<String, Object>> setStatus(@PathVariable Long id,
                                                         @RequestParam BookingStatus status) {
        Booking updated = service.setStatus(id, status);
        return message("Booking " + id + " status updated to " + status,
                Mapper.toBookingDto(updated),
                HttpStatus.OK);
    }

    // ADMIN: ta bort bokning
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/bookings/{id}")
    public ResponseEntity<Map<String, Object>> delete(@PathVariable Long id) {
        service.delete(id);
        return message("Booking " + id + " deleted successfully", null, HttpStatus.OK);
    }

    // Hjälpmetod för enhetliga svar
    private ResponseEntity<Map<String, Object>> message(String msg, Object data, HttpStatus status) {
        Map<String, Object> body = new HashMap<>();
        body.put("message", msg);
        if (data != null) body.put("data", data);
        return ResponseEntity.status(status).body(body);
    }
}
