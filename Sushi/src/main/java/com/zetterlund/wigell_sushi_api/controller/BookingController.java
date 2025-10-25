package com.zetterlund.wigell_sushi_api.controller;

import com.zetterlund.wigell_sushi_api.dto.BookingRequestDto;
import com.zetterlund.wigell_sushi_api.dto.BookingResponseDto;
import com.zetterlund.wigell_sushi_api.entity.Booking;
import com.zetterlund.wigell_sushi_api.service.BookingService;
import com.zetterlund.wigell_sushi_api.service.RoomService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/bookings")
public class BookingController {
    private static final Logger logger = LoggerFactory.getLogger(BookingController.class);
    private final BookingService bookingService;
    private final RoomService roomService;

    public BookingController(BookingService bookingService, RoomService roomService) {
        this.bookingService = bookingService;
        this.roomService = roomService;
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping
    public ResponseEntity<List<BookingResponseDto>> getBookingsByCustomerId(@RequestParam Integer customerId) {
        logger.info("getBookingsByCustomerId customerId={}", customerId);
        List<Booking> bookings = bookingService.getBookingsByCustomerId(customerId);

        List<BookingResponseDto> bookingDto = bookings.stream().map(booking -> {
            BookingResponseDto dto = new BookingResponseDto();
            dto.setBookingId(booking.getId());
            dto.setRoomName(booking.getRoom().getName());
            dto.setDate(booking.getDate());
            dto.setGuestCount(booking.getGuestCount());
            dto.setCatering(booking.getCatering());
            dto.setTechnicalEquipment(booking.isTechnicalEquipment());
            return dto;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(bookingDto);
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping
    public ResponseEntity<BookingResponseDto> addBooking(@RequestBody BookingRequestDto bookingDto) {
        logger.info("addBooking customerId={}", bookingDto.getCustomerId());

        Booking booking = bookingService.addBooking(bookingDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(mapToDto(booking));
    }

    @PreAuthorize("hasRole('USER')")
    @PatchMapping("/{bookingId}")
    public ResponseEntity<BookingResponseDto> updateBooking(
            @PathVariable Integer bookingId,
            @RequestBody BookingRequestDto bookingDto) {
        logger.info("updateBooking bookingId={}", bookingId);

        Booking existingBooking = bookingService.getBookingById(bookingId);

        if (bookingDto.getDate() != null) existingBooking.setDate(bookingDto.getDate());
        if (bookingDto.getGuestCount() != null && bookingDto.getGuestCount() > 0) existingBooking.setGuestCount(bookingDto.getGuestCount());
        if (bookingDto.getRoomId() != null) existingBooking.setRoom(roomService.getRoomById(bookingDto.getRoomId()));
        if (bookingDto.getCatering() != null) existingBooking.setCatering(bookingDto.getCatering());
        if (bookingDto.getTechnicalEquipment() != null) existingBooking.setTechnicalEquipment(bookingDto.getTechnicalEquipment());

        Booking updatedBooking = bookingService.updateBooking(bookingId, existingBooking);
        return ResponseEntity.ok(mapToDto(updatedBooking));
    }

    private BookingResponseDto mapToDto(Booking booking) {
        BookingResponseDto dto = new BookingResponseDto();
        dto.setBookingId(booking.getId());
        dto.setDate(booking.getDate());
        dto.setGuestCount(booking.getGuestCount());
        dto.setRoomName(booking.getRoom().getName());
        dto.setCatering(booking.getCatering());
        dto.setTechnicalEquipment(booking.isTechnicalEquipment());
        return dto;
    }
}
