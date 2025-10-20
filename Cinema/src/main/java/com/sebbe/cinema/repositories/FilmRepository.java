package com.sebbe.cinema.repositories;

import com.sebbe.cinema.entities.CinemaHall;
import com.sebbe.cinema.entities.Film;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FilmRepository extends JpaRepository<Film, Long> {

    List<Film> findByTitleIgnoreCase(@NotBlank @Size(max = 250) String title);
}
