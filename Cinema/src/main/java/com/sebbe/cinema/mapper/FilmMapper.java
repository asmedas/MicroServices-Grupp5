package com.sebbe.cinema.mapper;

import com.sebbe.cinema.dtos.FilmDto;
import com.sebbe.cinema.entities.Film;

public class FilmMapper {

    private FilmMapper() {}

    public static FilmDto toDto(Film film) {
        return new FilmDto(film.getAgeLimit(), film.getTitle(), film.getGenre());
    }

}
