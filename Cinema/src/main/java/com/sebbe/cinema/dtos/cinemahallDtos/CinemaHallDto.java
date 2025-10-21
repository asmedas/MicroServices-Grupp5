package com.sebbe.cinema.dtos.cinemahallDtos;

import com.sebbe.cinema.enums.TechnicalEquipment;

import java.util.List;

public record CinemaHallDto(
        long id,
        String name,
        int maxSeats
) {
}
