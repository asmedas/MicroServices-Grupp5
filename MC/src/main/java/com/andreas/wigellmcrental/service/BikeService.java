package com.andreas.wigellmcrental.service;

import com.andreas.wigellmcrental.entity.Bike;
import com.andreas.wigellmcrental.repository.BikeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BikeService {

    private static final Logger logger = LoggerFactory.getLogger(BikeService.class);
    private final BikeRepository repo;

    public BikeService(BikeRepository repo) {
        this.repo = repo;
    }

    // Hämta alla
    public List<Bike> all() {
        logger.debug("Fetching all bikes");
        return repo.findAll();
    }

    // Hämta en
    public Bike get(Long id) {
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
        Bike b = repo.findById(id).orElseThrow(() -> new RuntimeException("Bike not found"));
        repo.delete(b);
        logger.warn("Bike deleted: id={}, brand={}, model={}", b.getId(), b.getBrand(), b.getModel());
    }
}
