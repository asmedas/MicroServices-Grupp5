package com.sebbe.cinema.controllers;

import com.sebbe.cinema.dtos.screeningDtos.CustomerScreeningDto;
import com.sebbe.cinema.entities.Screening;
import com.sebbe.cinema.services.ScreeningService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public List<Screening> listScreenings() {
        return null;
    }

    @GetMapping("/screenings") //api/v1/screenings?filmId=42&date=2025-10-25
    public ResponseEntity<List<CustomerScreeningDto>> getScreeningsByFilmIdAndDate(
            @RequestParam Long filmId, @RequestParam LocalDate date) {
        return ResponseEntity.ok(screeningService.getScreeningsByFilmIdAndDate(filmId, date));
    }

    @PostMapping
    public String addScreening() {
        return "POST /api/v1/screenings";
    }

    @DeleteMapping("/{screeningId}")
    public String deleteScreening(@PathVariable Long screeningId) {
        return "DELETE /api/v1/screenings/" + screeningId;
    }

}
