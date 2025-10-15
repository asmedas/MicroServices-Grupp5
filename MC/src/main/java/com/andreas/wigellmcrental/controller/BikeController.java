package com.andreas.wigellmcrental.controller;

import com.andreas.wigellmcrental.dto.BikeDto;
import com.andreas.wigellmcrental.entity.Bike;
import com.andreas.wigellmcrental.mapper.Mapper;
import com.andreas.wigellmcrental.service.BikeService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class BikeController {

    private final BikeService service;

    public BikeController(BikeService service) {
        this.service = service;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/bikes")
    public List<BikeDto> all() {
        return service.all().stream().map(Mapper::toBikeDto).toList();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/bikes/{id}")
    public BikeDto get(@PathVariable Long id) {
        return Mapper.toBikeDto(service.get(id));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/bikes")
    public ResponseEntity<BikeDto> create(@Valid @RequestBody BikeDto in) {
        Bike saved = service.create(new Bike(in.brand(), in.model(), in.year(), in.pricePerDay(), in.available()));
        return ResponseEntity.created(URI.create("/api/v1/bikes/" + saved.getId()))
                .body(Mapper.toBikeDto(saved));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/bikes/{id}")
    public BikeDto update(@PathVariable Long id, @Valid @RequestBody BikeDto in) {
        Bike updated = service.update(id, new Bike(in.brand(), in.model(), in.year(), in.pricePerDay(), in.available()));
        return Mapper.toBikeDto(updated);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/bikes/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
