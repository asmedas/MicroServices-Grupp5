package com.strom.wigellPadel.controller;

import com.strom.wigellPadel.dto.AvailableDto;
import com.strom.wigellPadel.dto.BookingCreateDto;
import com.strom.wigellPadel.dto.BookingDto;
import com.strom.wigellPadel.dto.BookingUpdateDto;
import com.strom.wigellPadel.services.BookingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    private final BookingService bookingService;

    public UserController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @GetMapping("/availability")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<AvailableDto>> getAvailableSlots(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date,
            @RequestParam Long courtId) {
        logger.info("Mottog begäran om att hämta tillgängliga tider för datum {} och bana {}", date, courtId);
        List<AvailableDto> availableSlots = bookingService.availableTimeSlots(date, courtId);
        logger.debug("Returnerar {} tillgängliga tider för datum {} och bana {}", availableSlots.size(), date, courtId);
        return new ResponseEntity<>(availableSlots, HttpStatus.OK);
    }

    @PostMapping("/bookings")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<BookingDto> createBooking(@RequestBody BookingCreateDto dto) {
        logger.info("Mottog begäran om att skapa bokning för kund-ID {}", dto.customerId());
        BookingDto newBooking = bookingService.createBooking(dto);
        logger.debug("Skapade bokning med ID {} för kund-ID {}", newBooking.id(), dto.customerId());
        return new ResponseEntity<>(newBooking, HttpStatus.CREATED);
    }

    @PatchMapping("/bookings/{courtId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<BookingDto> patchBooking(@PathVariable Long courtId, @RequestBody BookingUpdateDto dto) {
        logger.info("Mottog begäran om att uppdatera bokning med ID {}", courtId);
        BookingDto updatedBooking = bookingService.patchBooking(courtId, dto);
        logger.debug("Uppdaterade bokning med ID {}", courtId);
        return new ResponseEntity<>(updatedBooking, HttpStatus.OK);
    }

    @GetMapping(value = "/bookings", params = "customerId")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<BookingDto>> getBookings(@RequestParam Long customerId) {
        logger.info("Mottog begäran om att hämta bokningar för kund-ID {}", customerId);
        List<BookingDto> bookingList = bookingService.getBookingsByCustomerId(customerId);
        logger.debug("Returnerar {} bokningar för kund-ID {}", bookingList.size(), customerId);
        return new ResponseEntity<>(bookingList, HttpStatus.OK);
    }
}