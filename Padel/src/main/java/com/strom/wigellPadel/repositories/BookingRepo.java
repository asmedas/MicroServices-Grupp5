package com.strom.wigellPadel.repositories;

import com.strom.wigellPadel.entities.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookingRepo extends JpaRepository<Booking, Integer> {
}
