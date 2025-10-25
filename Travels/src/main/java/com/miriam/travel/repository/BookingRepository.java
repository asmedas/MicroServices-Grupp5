package com.miriam.travel.repository;

import com.miriam.travel.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByCustomer_Id(String customerId);
}
