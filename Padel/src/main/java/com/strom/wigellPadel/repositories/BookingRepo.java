package com.strom.wigellPadel.repositories;

import com.strom.wigellPadel.entities.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface BookingRepo extends JpaRepository<Booking, Long> {

        @Query("SELECT b.timeSlot FROM Booking b WHERE b.courtId = :courtId AND b.date = :date")
        List<Integer> findBookedTimeSlotsByCourtIdAndDate(Long courtId, LocalDate date);

        @Query("SELECT b FROM Booking b WHERE b.courtId = :courtId AND b.date = :date AND b.timeSlot = :timeSlot")
        Optional<Booking> findByCourtIdAndDateAndTimeSlot(Long courtId, LocalDate date, int timeSlot);

}

