package com.sebbe.cinema.repositories;

import com.sebbe.cinema.entities.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    void deleteAllByCustomerId(long id);

    List<Booking> findByCustomerId(Long customerId);
}
