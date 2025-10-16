package com.zetterlund.wigell_sushi_api.service;

import com.zetterlund.wigell_sushi_api.entity.Booking;
import com.zetterlund.wigell_sushi_api.repository.BookingRepository;
import com.zetterlund.wigell_sushi_api.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookingService {
    private final BookingRepository bookingRepository;

    public BookingService(BookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;
    }

    public List<Booking> getBookingsByCustomerId(Integer customerId) {
        return bookingRepository.findByCustomerId(customerId);
    }

    public Booking addBooking(Booking booking) {
        return bookingRepository.save(booking);
    }

    public Booking updateBooking(Integer id, Booking updatedBooking) {
        Booking existingBooking = bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        existingBooking.setDate(updatedBooking.getDate());
        existingBooking.setGuestCount(updatedBooking.getGuestCount());
        existingBooking.setRoom(updatedBooking.getRoom());
        existingBooking.setBookingDetails(updatedBooking.getBookingDetails());
        existingBooking.setCustomer(updatedBooking.getCustomer());

        return bookingRepository.save(existingBooking);
    }

    public Booking getBookingById(Integer bookingId) {
        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id " + bookingId));
    }
}
