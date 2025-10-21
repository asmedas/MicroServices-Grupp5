package com.andreas.wigellmcrental.service;

import com.andreas.wigellmcrental.entity.Bike;
import com.andreas.wigellmcrental.repository.BikeRepository;
import com.andreas.wigellmcrental.repository.BookingRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class AvailabilityService {

    private final BikeRepository bikeRepo;
    private final BookingRepository bookingRepo;

    public AvailabilityService(BikeRepository bikeRepo, BookingRepository bookingRepo) {
        this.bikeRepo = bikeRepo;
        this.bookingRepo = bookingRepo;
    }

    // Hämtar motorcyklar som inte är bokade mellan två datum
    public List<Bike> getAvailable(LocalDate from, LocalDate to) {
        List<Bike> allBikes = bikeRepo.findAll();
        return allBikes.stream()
                .filter(b -> bookingRepo.findOverlapping(b.getId(), from, to).isEmpty())
                .toList();
    }

}
