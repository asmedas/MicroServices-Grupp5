package com.sebbe.cinema.services;

import com.sebbe.cinema.dtos.cinemahallDtos.CinemaHallDto;
import com.sebbe.cinema.dtos.cinemahallDtos.CreateCinemaHallDto;
import com.sebbe.cinema.dtos.cinemahallDtos.UpdateCinemaHallDto;
import com.sebbe.cinema.entities.CinemaHall;
import com.sebbe.cinema.entities.Screening;
import com.sebbe.cinema.exceptions.AlreadyExistsError;
import com.sebbe.cinema.exceptions.NoMatchException;
import com.sebbe.cinema.exceptions.UnexpectedError;
import com.sebbe.cinema.mappers.CinemaHallMapper;
import com.sebbe.cinema.repositories.CinemaHallRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@PreAuthorize("hasRole('ADMIN')")
public class CinemaHallService {

    private final CinemaHallRepository cinemaHallRepository;
    private static final Logger log = LoggerFactory.getLogger(CinemaHallService.class);

    public CinemaHallService(CinemaHallRepository cinemaHallRepository) {
        this.cinemaHallRepository = cinemaHallRepository;
    }

    @Transactional(readOnly = true)
    public List<CinemaHallDto> findAll(){
        log.debug("Retrieving all cinema halls");
        return cinemaHallRepository.findAll().stream()
                .map(CinemaHallMapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public CinemaHallDto findById(Long id){
        log.debug("Retrieving cinema hall with id: {}", id);
        return cinemaHallRepository.findById(id)
                .map(CinemaHallMapper::toDto)
                .orElseThrow(() -> {
                    log.error("Cinema hall with id {} not found", id);
                    return new NoMatchException("Cinema hall not found");
                });
    }

    public CinemaHall findEntityById(Long id){
        return cinemaHallRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Cinema hall with id {} not found", id);
                    return new NoMatchException("Cinema hall not found");
                });
    }


    public CinemaHallDto createCinemaHall(CreateCinemaHallDto cinemaHallDto){
        log.debug("Saving cinema hall: {}", cinemaHallDto);
        if(!cinemaHallRepository.findByNameIgnoreCase(cinemaHallDto.name()).isEmpty()){
            log.error("CinemaHall name already exists");
            throw new AlreadyExistsError("CinemaHall name already exists");
        }
        return CinemaHallMapper.toDto(cinemaHallRepository.save(CinemaHallMapper.toEntity(cinemaHallDto)));
    }

    public CinemaHallDto updateCinemaHall(Long id, UpdateCinemaHallDto updateCinemaHallDto){
        log.debug("Updating cinema hall: {}", updateCinemaHallDto);
        CinemaHall cinemaHall = cinemaHallRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Cinema hall with id {} not found", id);
                    return new NoMatchException("Cinema hall not found");
                });
        cinemaHall.setName(updateCinemaHallDto.name());
        cinemaHall.setMaxSeats(updateCinemaHallDto.maxSeats());
        return CinemaHallMapper.toDto(cinemaHallRepository.save(cinemaHall));
    }


    public void deleteCinemaHall(Long id){
        CinemaHall cinemaHall = cinemaHallRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Cinema hall with id {} not found", id);
                    return new NoMatchException("Cinema hall not found");
                });
        try{
            cinemaHallRepository.delete(cinemaHall);
        } catch (DataAccessException e){
            log.error("Database error deleting cinema hall", e);
            throw new UnexpectedError("Database error deleting cinema hall " + e);
        }
    }


}
