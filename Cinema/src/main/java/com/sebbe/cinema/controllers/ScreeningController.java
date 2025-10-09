package com.sebbe.cinema.controllers;

import com.sebbe.cinema.entities.Screening;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/screenings")
public class ScreeningController {

    @GetMapping
    public List<Screening> listScreenings() {
        return null;
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
