package com.sebbe.cinema.services;

import com.sebbe.cinema.entities.Booking;
import com.sebbe.cinema.exceptions.NoMatchException;
import com.sebbe.cinema.exceptions.UnexpectedError;
import com.sebbe.cinema.repositories.BookingRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class BookingService {

    private static final Logger log = LoggerFactory.getLogger(BookingService.class);
    private final BookingRepository bookingRepository;

    public BookingService(BookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;
    }

    public void deleteBooking(Long bookingId){
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> {
                    log.error("Booking not found");
                    return new NoMatchException("Booking not found");
                });
        booking.removeBookingFromConnections();
        try{
            bookingRepository.delete(booking);
        } catch (DataAccessException e){
            log.error("Error removing Booking", e);
            throw new UnexpectedError("Error removing Booking " + e);
        }
    }
}
