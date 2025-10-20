package com.sebbe.cinema.controllers;

import com.sebbe.cinema.dtos.filmDtos.CreateFilmDto;
import com.sebbe.cinema.dtos.filmDtos.FilmDto;
import com.sebbe.cinema.entities.Film;
import com.sebbe.cinema.services.FilmService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/movies")
public class FilmController {

    private final FilmService filmService;
    private static final Logger log = LoggerFactory.getLogger(FilmController.class);

    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    public ResponseEntity<List<FilmDto>> listFilms() {
        return ResponseEntity.ok(filmService.findAll());
    }

    @GetMapping("/{movieId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<FilmDto> getFilm(@PathVariable Long movieId) {
        return ResponseEntity.ok(filmService.findById(movieId));
    }

    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<FilmDto> addFilm(@RequestBody @Valid CreateFilmDto dto) {
        log.info("User tries adding new film {}", dto);
        FilmDto created = filmService.createFilm(dto);
        URI location = URI.create("/api/v1/films/" + created.id());
        return ResponseEntity.created(location).body(created);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Void> deleteFilm(@PathVariable Long id) {
        log.info("User tries deleting film {}", id);
        filmService.deleteFilm(id);
        return ResponseEntity.ok().build();
    }

}
