package com.sebbe.cinema.dtos.screeningDtos;

import com.sebbe.cinema.dtos.cinemahallDtos.CinemaHallDto;
import com.sebbe.cinema.dtos.filmDtos.FilmDto;
import com.sebbe.cinema.enums.Type;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record CustomerScreeningDto(
        LocalDate date,
        List<Type> type,
        FilmDto film,
        CinemaHallDto cinemaHall
) {

}
