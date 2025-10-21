package com.zetterlund.wigell_sushi_api.controller;

import com.zetterlund.wigell_sushi_api.dto.BookingRequestDto;
import com.zetterlund.wigell_sushi_api.dto.BookingResponseDto;
import com.zetterlund.wigell_sushi_api.entity.Booking;
import com.zetterlund.wigell_sushi_api.service.BookingService;
import com.zetterlund.wigell_sushi_api.service.RoomService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/bookings")
public class BookingController {
    private final BookingService bookingService;
    private final RoomService roomService;

    public BookingController(BookingService bookingService, RoomService roomService) {
        this.bookingService = bookingService;
        this.roomService = roomService;
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<BookingResponseDto>> getBookingsByCustomerId(@RequestParam Integer customerId) {
        List<Booking> bookings = bookingService.getBookingsByCustomerId(customerId);

        List<BookingResponseDto> bookingDto = bookings.stream().map(booking -> {
            BookingResponseDto dto = new BookingResponseDto();
            dto.setBookingId(booking.getId());
            dto.setRoomName(booking.getRoom().getName());
            dto.setDate(booking.getDate());
            dto.setGuestCount(booking.getGuestCount());
            return dto;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(bookingDto);
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<BookingResponseDto> addBooking(@RequestBody BookingRequestDto bookingDto) {
        Booking booking = new Booking();
        booking.setGuestCount(bookingDto.getGuestCount());
        booking.setDate(bookingDto.getDate());
        // Om behövligt - Här kan vi sätta Room och Customer från DTO

        Booking createdBooking = bookingService.addBooking(booking);

        BookingResponseDto responseDto = new BookingResponseDto();
        responseDto.setBookingId(createdBooking.getId());
        responseDto.setDate(createdBooking.getDate());
        responseDto.setGuestCount(createdBooking.getGuestCount());
        responseDto.setRoomName(createdBooking.getRoom().getName());

        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Booking> updateBooking(
            @PathVariable Integer bookingId,
            @RequestBody BookingRequestDto bookingDto) {

        // Hämta befintlig bokning
        Booking existingBooking = bookingService.getBookingById(bookingId);

        if (bookingDto.getDate() != null) {
            existingBooking.setDate(bookingDto.getDate());
        }
        if (bookingDto.getGuestCount() > 0) {
            existingBooking.setGuestCount(bookingDto.getGuestCount());
        }
        if (bookingDto.getRoomId() != null) {
            existingBooking.setRoom(roomService.getRoomById(bookingDto.getRoomId()));
        }

        // Exempel: önskad förtäring eller teknisk utrustning skulle också mappas här

        Booking updatedBooking = bookingService.updateBooking(bookingId, existingBooking);
        return ResponseEntity.ok(updatedBooking);
    }
}
