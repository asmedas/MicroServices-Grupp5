package com.sebbe.cinema.services;

import com.sebbe.cinema.entities.CinemaHall;
import com.sebbe.cinema.exceptions.NoMatchException;
import com.sebbe.cinema.repositories.CinemaHallRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
    public List<CinemaHall> findAll(){
        log.debug("Retrieving all cinema halls");
        return cinemaHallRepository.findAll();
    }

    @Transactional(readOnly = true)
    public CinemaHall findById(Long id){
        log.debug("Retrieving cinema hall with id: {}", id);
        return cinemaHallRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Cinema hall with id {} not found", id);
                    return new NoMatchException("Cinema hall not found");
                });
    }


}
