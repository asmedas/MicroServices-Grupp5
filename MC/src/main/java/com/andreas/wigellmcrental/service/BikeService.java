package com.andreas.wigellmcrental.service;

import com.andreas.wigellmcrental.entity.Bike;
import com.andreas.wigellmcrental.entity.BookingStatus;
import com.andreas.wigellmcrental.repository.BikeRepository;
import com.andreas.wigellmcrental.repository.BookingRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BikeService {

    private static final Logger logger = LoggerFactory.getLogger(BikeService.class);
    private final BikeRepository repo;
    private final BookingRepository bookingRepo;

    public BikeService(BikeRepository repo, BookingRepository bookingRepo) {
        this.repo = repo;
        this.bookingRepo = bookingRepo;
    }

    // Hämta alla
    public List<Bike> all() {
        logger.debug("Fetching all bikes");
        return repo.findAll();
    }

    // Hämta en
    public Bike get(Long id) {
        logger.debug("Fetching bike with id {}", id);
        return repo.findById(id).orElseThrow(() -> new RuntimeException("Bike not found"));
    }

    // Skapa ny
    public Bike create(Bike b) {
        Bike saved = repo.save(b);
        logger.info("Bike created: id={}, brand={}, model={}, price/day={}",
                saved.getId(), saved.getBrand(), saved.getModel(), saved.getPricePerDay());
        return saved;
    }

    // Uppdatera befintlig
    public Bike update(Long id, Bike in) {
        Bike existing = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Bike not found"));

        existing.setBrand(in.getBrand());
        existing.setModel(in.getModel());
        existing.setYear(in.getYear());
        existing.setPricePerDay(in.getPricePerDay());
        existing.setAvailable(in.isAvailable());

        Bike saved = repo.save(existing);
        logger.info("Bike updated: id={}, brand={}, model={}, available={}",
                id, saved.getBrand(), saved.getModel(), saved.isAvailable());
        return saved;
    }

    // Ta bort
    public void delete(Long id) {
        Bike b = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Bike not found"));

        // 🔒 Kontrollera om hojen är uthyrd
        boolean hasActiveBooking = bookingRepo.existsByBike_IdAndStatus(b.getId(), BookingStatus.ACTIVE);
        if (hasActiveBooking) {
            logger.warn("Attempted to delete bike that is currently rented: id={}, brand={}, model={}",
                    b.getId(), b.getBrand(), b.getModel());
            throw new IllegalStateException("Kan inte ta bort en motorcykel som är uthyrd.");
        }

        repo.delete(b);
        logger.warn("Bike deleted: id={}, brand={}, model={}", b.getId(), b.getBrand(), b.getModel());
    }
}
