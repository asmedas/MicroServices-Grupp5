package com.miriam.travel.controller;

import com.miriam.travel.dto.destination.DestinationCreateUpdateRequest;
import com.miriam.travel.entity.Destination;
import com.miriam.travel.service.DestinationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/destinations")
public class DestinationController {

    private static final Logger log = LoggerFactory.getLogger(DestinationController.class);
    private final DestinationService destinationService;

    public DestinationController(DestinationService destinationService) {
        this.destinationService = destinationService;
    }


    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Destination> create(@RequestBody DestinationCreateUpdateRequest req,
                                              UriComponentsBuilder uriBuilder) {
        Destination created = destinationService.create(req);
        String user = getCurrentUser();
        log.info("{} created destination id={} city={} country={} hotel={} pricePerWeekSek={}",
                user, created.getId(), created.getCity(), created.getCountry(),
                created.getHotelName(), created.getPricePerWeekSek());

        URI location = uriBuilder.path("/api/v1/destinations/{id}")
                .buildAndExpand(created.getId()).toUri();
        return ResponseEntity.created(location).body(created);
    }


    @PutMapping("/{destinationId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Destination> update(@PathVariable Long destinationId,
                                              @RequestBody DestinationCreateUpdateRequest req) {
        Destination updated = destinationService.update(destinationId, req);
        String user = getCurrentUser();
        log.info("{} updated destination id={} city={} country={} hotel={} pricePerWeekSek={}",
                user, updated.getId(), updated.getCity(), updated.getCountry(),
                updated.getHotelName(), updated.getPricePerWeekSek());
        return ResponseEntity.ok(updated);
    }


    @DeleteMapping("/{destinationId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long destinationId) {
        destinationService.delete(destinationId);
        String user = getCurrentUser();
        log.info("{} deleted destination id={}", user, destinationId);
        return ResponseEntity.noContent().build();
    }


    @GetMapping
    public ResponseEntity<List<Destination>> listAll() {
        String user = getCurrentUser();
        log.debug("{} listed all destinations", user);
        return ResponseEntity.ok(destinationService.listAll());
    }


    @GetMapping("/{destinationId}")
    public ResponseEntity<Destination> get(@PathVariable Long destinationId) {
        String user = getCurrentUser();
        log.debug("{} fetched destination id={}", user, destinationId);
        return ResponseEntity.ok(destinationService.get(destinationId));
    }


    private String getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return (auth != null) ? auth.getName() : "anonymous";
    }
}
