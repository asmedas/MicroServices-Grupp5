package com.sebbe.cinema.controllers;

import com.sebbe.cinema.dtos.cinemahallDtos.CinemaHallDto;
import com.sebbe.cinema.dtos.cinemahallDtos.CreateCinemaHallDto;
import com.sebbe.cinema.dtos.cinemahallDtos.UpdateCinemaHallDto;
import com.sebbe.cinema.dtos.filmDtos.FilmDto;
import com.sebbe.cinema.entities.CinemaHall;
import com.sebbe.cinema.services.CinemaHallService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/rooms")
@PreAuthorize("hasRole('ROLE_ADMIN')")
public class CinemaHallController {

    private final CinemaHallService cinemaHallService;
    private static final Logger log = LoggerFactory.getLogger(CinemaHallController.class);

    public CinemaHallController(CinemaHallService cinemaHallService) {
        this.cinemaHallService = cinemaHallService;
    }

    @GetMapping
    public List<CinemaHallDto> listRooms() {
        return cinemaHallService.findAll();
    }

    @GetMapping("/{roomId}")
    public CinemaHallDto getRoom(@PathVariable Long roomId) {
        return cinemaHallService.findById(roomId);
    }

    @PostMapping
    public ResponseEntity<CinemaHallDto> createRoom(@RequestBody @Valid CreateCinemaHallDto dto) {
        log.info("Admin creates a cinemahall {}", dto);
        CinemaHallDto created = cinemaHallService.createCinemaHall(dto);
        URI location = URI.create("/api/v1/films/" + created.id());
        return ResponseEntity.created(location).body(created);
    }

    @PutMapping("/{roomId}")
    public ResponseEntity<CinemaHallDto> updateRoom(
            @PathVariable Long roomId,
            @RequestBody @Valid UpdateCinemaHallDto dto) {
        return ResponseEntity.ok(cinemaHallService.updateCinemaHall(roomId, dto));
    }

}
