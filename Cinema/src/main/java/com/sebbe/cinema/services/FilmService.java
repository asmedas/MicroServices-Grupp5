package com.sebbe.cinema.services;

import com.sebbe.cinema.dtos.filmDtos.CreateMovieDto;
import com.sebbe.cinema.dtos.filmDtos.FilmDto;
import com.sebbe.cinema.entities.Film;
import com.sebbe.cinema.exceptions.NoMatchException;
import com.sebbe.cinema.exceptions.UnexpectedError;
import com.sebbe.cinema.mappers.FilmMapper;
import com.sebbe.cinema.repositories.FilmRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class FilmService {

    private final FilmRepository filmRepository;
    private static final Logger log = LoggerFactory.getLogger(FilmService.class);

    public FilmService(FilmRepository filmRepository) {
        this.filmRepository = filmRepository;
    }


    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @Transactional(readOnly = true)
    public List<Film> findAll() {
        List<Film> films = filmRepository.findAll();
        log.debug("Retrieved {} films from repository", films.size());
        return films;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional(readOnly = true)
    public Film findById(Long id) {
        log.debug("Looking up film with id {}", id);

        return filmRepository.findById(id)
                .map(film -> {
                    log.debug("Found film with id {}: {}", id, film.getTitle());
                    return film;
                })
                .orElseThrow(() -> {
                    log.warn("No film found with id {}", id);
                    return new NoMatchException("No film found with id " + id);
                });
    }

    @PreAuthorize("hasRole('ADMIN')")
    public FilmDto createFilm(CreateMovieDto dto) {
        log.debug("Creating new film with title {}", dto.title());
        try {
            Film film = new Film(dto.ageLimit(), dto.title(), dto.genre());
            filmRepository.save(film);
            return FilmMapper.toDto(film);
        } catch (DataAccessException e) {
            log.error("Database error creating film", e);
            throw new UnexpectedError("Database error creating film " + e);
        } catch (Exception e) {
            log.error("Unexpected error creating film", e);
            throw new UnexpectedError("Unexpected error creating film " + e);
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void deleteFilm(Long id) {
        log.debug("Deleting film with id {}", id);
        if(filmRepository.findById(id).isEmpty()) {
            throw new NoMatchException("No film found with id " + id);
        }
        try {
            filmRepository.deleteById(id);
        } catch (DataAccessException e) {
            log.error("Database error deleting film", e);
            throw new UnexpectedError("Database error deleting film " + e);
        } catch (Exception e) {
            log.error("Unexpected error deleting film", e);
            throw new UnexpectedError("Unexpected error deleting film " + e);
        }
    }

}
