package com.andreas.wigellmcrental.controller;

import com.andreas.wigellmcrental.dto.BikeDto;
import com.andreas.wigellmcrental.entity.Bike;
import com.andreas.wigellmcrental.mapper.Mapper;
import com.andreas.wigellmcrental.service.BikeService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
public class BikeController {

    private final BikeService service;

    public BikeController(BikeService service) {
        this.service = service;
    }

    // ADMIN: Lista alla motorcyklar
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/bikes")
    public List<BikeDto> all() {
        return service.all().stream().map(Mapper::toBikeDto).toList();
    }

    // ADMIN: Hämta specifik motorcykel
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/bikes/{id}")
    public BikeDto get(@PathVariable Long id) {
        return Mapper.toBikeDto(service.get(id));
    }

    // ADMIN: Lägg till ny motorcykel
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/bikes")
    public ResponseEntity<Map<String, Object>> create(@Valid @RequestBody BikeDto in) {
        Bike saved = service.create(new Bike(in.brand(), in.model(), in.year(), in.pricePerDay(), in.available()));
        return message("Bike created successfully",
                Mapper.toBikeDto(saved),
                HttpStatus.CREATED,
                "/api/v1/bikes/" + saved.getId());
    }

    // ADMIN: Uppdatera motorcykel
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/bikes/{id}")
    public ResponseEntity<Map<String, Object>> update(@PathVariable Long id, @Valid @RequestBody BikeDto in) {
        Bike updated = service.update(id, new Bike(in.brand(), in.model(), in.year(), in.pricePerDay(), in.available()));
        return message("Bike " + id + " updated successfully",
                Mapper.toBikeDto(updated),
                HttpStatus.OK,
                null);
    }

    // ADMIN: Ta bort motorcykel
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/bikes/{id}")
    public ResponseEntity<Map<String, Object>> delete(@PathVariable Long id) {
        service.delete(id);
        return message("Bike " + id + " deleted successfully",
                null,
                HttpStatus.OK,
                null);
    }

    // Hjälpmetod för enhetliga svar
    private ResponseEntity<Map<String, Object>> message(String msg, Object data, HttpStatus status, String location) {
        Map<String, Object> body = new HashMap<>();
        body.put("message", msg);
        if (data != null) body.put("data", data);
        if (location != null) body.put("location", location);
        return ResponseEntity.status(status).body(body);
    }
}
