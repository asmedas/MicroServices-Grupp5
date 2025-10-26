package com.sebbe.cinema.services;

import com.sebbe.cinema.dtos.filmDtos.CreateFilmDto;
import com.sebbe.cinema.dtos.filmDtos.FilmDto;
import com.sebbe.cinema.entities.Film;
import com.sebbe.cinema.exceptions.AlreadyExistsError;
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


    @Transactional(readOnly = true)
    public List<FilmDto> findAll() {
        List<Film> films = filmRepository.findAll();
        log.debug("Retrieved {} films from repository", films.size());
        return films.stream()
                .map(FilmMapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public FilmDto findById(Long id) {
        log.debug("Looking up film with id {}", id);
        return filmRepository.findById(id)
                .map(film -> {
                    log.debug("Found film with id {}: {}", id, film.getTitle());
                    return FilmMapper.toDto(film);
                })
                .orElseThrow(() -> {
                    log.warn("No film found with id {}", id);
                    return new NoMatchException("No film found with id " + id);
                });
    }

    public FilmDto createFilm(CreateFilmDto dto) {
        log.debug("Creating new film with title {}", dto.title());
        if(!filmRepository.findByTitleIgnoreCase(dto.title()).isEmpty()){
            throw new AlreadyExistsError("Film with title " + dto.title() + " already exists");
        }
        if(dto.ageLimit() == null){
            try {
                Film film = new Film(dto.title(), dto.genre(), dto.length());
                filmRepository.save(film);
                return FilmMapper.toDto(film);
            } catch (DataAccessException e) {
                log.error("Database error creating film", e);
                throw new UnexpectedError("Database error creating film " + e);
            } catch (Exception e) {
                log.error("Unexpected error creating film", e);
                throw new UnexpectedError("Unexpected error creating film " + e);
            }
        } else {
            try {
                Film film = new Film(dto.ageLimit(), dto.title(), dto.genre(), dto.length());
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
    }

    public void deleteFilm(Long id) {
        log.debug("Deleting film with id {}", id);
        Film film = filmRepository.findById(id)
                .orElseThrow(() -> new NoMatchException("No film found with id " + id));
        try {
            filmRepository.delete(film);
        } catch (DataAccessException e) {
            log.error("Database error deleting film", e);
            throw new UnexpectedError("Database error deleting film " + e);
        } catch (Exception e) {
            log.error("Unexpected error deleting film", e);
            throw new UnexpectedError("Unexpected error deleting film " + e);
        }
    }

}
