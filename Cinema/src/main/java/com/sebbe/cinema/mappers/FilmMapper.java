package com.sebbe.cinema.mappers;

import com.sebbe.cinema.dtos.filmDtos.FilmDto;
import com.sebbe.cinema.entities.Film;

public class FilmMapper {

    private FilmMapper() {}

    public static FilmDto toDto(Film film) {
        return new FilmDto(film.getAgeLimit(), film.getTitle(), film.getGenre(), film.getId());
    }

}
