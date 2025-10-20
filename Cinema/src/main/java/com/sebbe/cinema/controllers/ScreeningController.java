package com.sebbe.cinema.controllers;

import com.sebbe.cinema.dtos.screeningDtos.CreateScreeningDto;
import com.sebbe.cinema.dtos.screeningDtos.CustomerScreeningDto;
import com.sebbe.cinema.dtos.screeningDtos.ScreeningDto;
import com.sebbe.cinema.services.ScreeningService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/screenings")
public class ScreeningController {

    private final ScreeningService screeningService;

    public ScreeningController(ScreeningService screeningService) {
        this.screeningService = screeningService;
    }

    @GetMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public List<ScreeningDto> listScreenings() {
        return screeningService.findAll();
    }

    @GetMapping("/screenings") //api/v1/screenings?filmId=42&date=2025-10-25
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<List<CustomerScreeningDto>> getScreeningsByFilmIdAndDate(
            @RequestParam Long filmId, @RequestParam LocalDate date) {
        return ResponseEntity.ok(screeningService.getScreeningsByFilmIdAndDate(filmId, date));
    }

    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ScreeningDto> createScreening(@RequestBody @Valid CreateScreeningDto dto) {
        return ResponseEntity.created(URI.create("/api/v1/screenings")).body(screeningService.createScreening(dto));
    }

    @DeleteMapping("/{screeningId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> deleteScreening(@PathVariable Long screeningId) {
        screeningService.deleteScreening(screeningId);
        return ResponseEntity.noContent().build();
    }

}
