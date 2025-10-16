package com.sebbe.cinema.mappers;

import com.sebbe.cinema.dtos.cinemahallDtos.CinemaHallDto;
import com.sebbe.cinema.dtos.cinemahallDtos.CreateCinemaHallDto;
import com.sebbe.cinema.entities.CinemaHall;

public class CinemaHallMapper {

    private CinemaHallMapper(){}

    public static CinemaHallDto toDto(CinemaHall cinemaHall){
        return new CinemaHallDto(
                cinemaHall.getId(),
                cinemaHall.getName(),
                cinemaHall.getMaxSeats()
        );
    }

    public static CinemaHall toEntity(CreateCinemaHallDto cinemaHallDto) {
        return new CinemaHall(
                cinemaHallDto.name(),
                cinemaHallDto.maxSeats()
        );
    }
}
