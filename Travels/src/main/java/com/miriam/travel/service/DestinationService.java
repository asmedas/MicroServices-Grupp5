package com.miriam.travel.service;

import com.miriam.travel.dto.destination.DestinationCreateUpdateRequest;
import com.miriam.travel.entity.Destination;
import com.miriam.travel.repository.DestinationRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DestinationService {

    private final DestinationRepository destinationRepository;
    private static final Logger log = LoggerFactory.getLogger(DestinationService.class);

    public DestinationService(DestinationRepository destinationRepository) {
        this.destinationRepository = destinationRepository;
    }

    @Transactional
    public List<Destination> listAll() {
        String user = getCurrentUser();
        log.debug("{} listed all destinations", user);
        return destinationRepository.findAll();
    }

    @Transactional
    public Destination create(DestinationCreateUpdateRequest req) {
        Destination d = new Destination(req.hotelName, req.city, req.country, req.pricePerWeekSek);
        Destination saved = destinationRepository.save(d);
        String user = getCurrentUser();
        log.info("{} created destination id={} city={} country={} hotel={} pricePerWeekSek={}",
                user, saved.getId(), saved.getCity(), saved.getCountry(), saved.getHotelName(), saved.getPricePerWeekSek());
        return saved;
    }

    @Transactional
    public Destination update(Long id, DestinationCreateUpdateRequest req) {
        Destination d = destinationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Destination not found: " + id));
        d.setHotelName(req.hotelName);
        d.setCity(req.city);
        d.setCountry(req.country);
        d.setPricePerWeekSek(req.pricePerWeekSek);
        Destination saved = destinationRepository.save(d);
        String user = getCurrentUser();
        log.info("{} updated destination id={} city={} country={} hotel={} pricePerWeekSek={}",
                user, id, saved.getCity(), saved.getCountry(), saved.getHotelName(), saved.getPricePerWeekSek());
        return saved;
    }

    @Transactional
    public void delete(Long id) {
        if (!destinationRepository.existsById(id)) {
            throw new EntityNotFoundException("Destination not found: " + id);
        }
        destinationRepository.deleteById(id);
        String user = getCurrentUser();
        log.info("{} deleted destination id={}", user, id);
    }

    @Transactional
    public Destination get(Long id) {
        String user = getCurrentUser();
        log.debug("{} fetched destination id={}", user, id);
        return destinationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Destination not found: " + id));
    }


    private String getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return (auth != null) ? auth.getName() : "anonymous";
    }
}