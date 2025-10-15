package com.strom.wigellPadel.services;

import com.strom.wigellPadel.dto.BookingDto;
import com.strom.wigellPadel.entities.Booking;
import com.strom.wigellPadel.mapper.BookingMapper;
import com.strom.wigellPadel.repositories.BookingRepo;
import com.strom.wigellPadel.repositories.CourtRepo;
import com.strom.wigellPadel.repositories.CustomerRepo;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookingService {

    private final BookingRepo bookingRepo;
    private final CourtRepo courtRepo;
    private final CustomerRepo customerRepo;

    public BookingService(BookingRepo bookingRepo, CourtRepo courtRepo, CustomerRepo customerRepo) {
        this.bookingRepo = bookingRepo;
        this.courtRepo = courtRepo;
        this.customerRepo = customerRepo;
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteBooking(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Id är null");
        }
        Booking booking = bookingRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Bokning med id " + id + " hittades inte"));
        bookingRepo.delete(booking);
    }

    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('ADMIN')")
    public List<BookingDto> getAllBookings() {
        return bookingRepo.findAll().stream()
                .map(BookingMapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('ADMIN')")
    public BookingDto getBooking(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Id är null");
        }
        return bookingRepo.findById(id)
                .map(BookingMapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException("Bokning med id " + id + " hittades inte"));
    }

}
