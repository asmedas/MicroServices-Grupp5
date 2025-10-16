package com.sebbe.cinema.services;

import com.sebbe.cinema.dtos.screeningDtos.CreateScreeningDto;
import com.sebbe.cinema.dtos.screeningDtos.CustomerScreeningDto;
import com.sebbe.cinema.dtos.screeningDtos.ScreeningDto;
import com.sebbe.cinema.entities.*;
import com.sebbe.cinema.enums.Type;
import com.sebbe.cinema.exceptions.NoMatchException;
import com.sebbe.cinema.exceptions.UnexpectedError;
import com.sebbe.cinema.mappers.ScreeningMapper;
import com.sebbe.cinema.repositories.CinemaHallRepository;
import com.sebbe.cinema.repositories.FilmRepository;
import com.sebbe.cinema.repositories.ScreeningRepository;
import com.sebbe.cinema.repositories.TicketRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ScreeningService {

    private static final Logger log = LoggerFactory.getLogger(ScreeningService.class);
    private final ScreeningRepository screeningRepository;
    private final FilmRepository filmRepository;
    private final CinemaHallRepository cinemaHallRepository;
    private final CallCurrencyApiService callCurrencyApiService;

    public ScreeningService(ScreeningRepository screeningRepository, TicketRepository ticketRepository,
                            CinemaHallRepository cinemaHallRepository, FilmRepository filmRepository,
                            CallCurrencyApiService callCurrencyApiService) {
        this.screeningRepository = screeningRepository;
        this.cinemaHallRepository = cinemaHallRepository;
        this.filmRepository = filmRepository;
        this.callCurrencyApiService = callCurrencyApiService;
    }

    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public List<ScreeningDto> findAll(){
        log.debug("Fetching all screenings");
        return screeningRepository.findAll().stream()
                .map(ScreeningMapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public ScreeningDto getScreeningById(Long id, LocalDate localDate){
        log.debug("Fetching screening with movie id: {} on date: {}", id, localDate);
        Screening screening = screeningRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Screening with id {} not found", id);
                    return new NoMatchException("Screening not found");
                });
        return ScreeningMapper.toDto(screening);
    }

    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('ROLE_USER')")
    public List<CustomerScreeningDto> getScreeningsByFilmIdAndDate(Long filmId, LocalDate date){
        log.debug("Fetching screenings by filmId: {} and date: {}", filmId, date);
        return screeningRepository.getScreeningsByFilmIdAndDate(filmId, date).stream()
                .map(ScreeningMapper::toCustomerDto)
                .toList();
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ScreeningDto createScreening(CreateScreeningDto createScreeningDto){
        log.debug("Creating screening with filmId: {}, speakerName: {}, cinemaHallId: {}",
                createScreeningDto.filmId(), createScreeningDto.speakerName(), createScreeningDto.cinemaHallId());

        Optional<Film> film = filmRepository.findById(createScreeningDto.filmId());
        if(film.isEmpty() && createScreeningDto.speakerName() == null) {
            throw new IllegalStateException("Film and speaker name cannot be null, select one of them.");
        }

        CinemaHall cinemaHall = cinemaHallRepository.findById(createScreeningDto.cinemaHallId())

                .orElseThrow(() -> new NoMatchException("Cinema hall with id " + createScreeningDto.cinemaHallId() + " not found"));
        if(createScreeningDto.speakerName() != null){
            log.debug("Creating screening with speaker name: {}", createScreeningDto.speakerName());
            Screening screening = new Screening(createScreeningDto.date(),
                     createScreeningDto.speakerName(), cinemaHall, List.of(Type.SPEAKER));
            screening.setPriceSek(calculatePriceSek(cinemaHall));
            screening.setPriceUsd(calculatePriceUsd(screening.getPriceSek()));
            screeningRepository.save(screening);
            return ScreeningMapper.toDto(screening);
        }

        Film film1 = film.orElseThrow();
        log.debug("Creating screening with film: {}", film1.getTitle());
        Screening screening = new Screening(createScreeningDto.date(), film1, cinemaHall
        , List.of(Type.FILM));
        screening.setPriceSek(calculatePriceSek(cinemaHall));
        screening.setPriceUsd(calculatePriceUsd(screening.getPriceSek()));
        screeningRepository.save(screening);
        return ScreeningMapper.toDto(screening);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public void deleteScreening(Long id){
        log.debug("Deleting screening with id {}", id);
        Screening screening = screeningRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Screening with id {} not found", id);
                    return new NoMatchException("Screening not found");
                });
        try{
            screening.removeScreeningFromConnections();
            screeningRepository.delete(screening);
        } catch (DataAccessException e){
            log.error("Error deleting screening", e);
            throw new UnexpectedError("Error deleting screening " + e);
        }
    }

    public BigDecimal calculatePriceSek(CinemaHall cinemaHall){
        return BigDecimal.valueOf(cinemaHall.getMaxSeats() * 100);
    }

    public BigDecimal calculatePriceUsd(BigDecimal priceSek){
        return callCurrencyApiService.convertFromSEKToUsd(priceSek);
    }

}
