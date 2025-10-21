package com.sebbe.cinema.mappers;

import com.sebbe.cinema.dtos.screeningDtos.CustomerScreeningDto;
import com.sebbe.cinema.dtos.screeningDtos.ScreeningDto;
import com.sebbe.cinema.entities.Screening;

public class ScreeningMapper {

    private ScreeningMapper() {}

    public static ScreeningDto toFilmDto(Screening screening) {
        return new ScreeningDto(
                screening.getId(),
                screening.getDate(),
                screening.getType(),
                FilmMapper.toDto(screening.getFilm()),
                null,
                CinemaHallMapper.toDto(screening.getCinemaHall())
        );
    }

    public static ScreeningDto toSpeakerDto(Screening screening){
        return new ScreeningDto(
                screening.getId(),
                screening.getDate(),
                screening.getType(),
                null,
                screening.getSpeakerName(),
                CinemaHallMapper.toDto(screening.getCinemaHall())
        );
    }

    public static CustomerScreeningDto toCustomerDto(Screening screening) {
        return new CustomerScreeningDto(
                screening.getDate(),
                screening.getType(),
                FilmMapper.toDto(screening.getFilm()),
                CinemaHallMapper.toDto(screening.getCinemaHall())
        );
    }
}
