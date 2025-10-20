package com.sebbe.cinema.dtos.screeningDtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.sebbe.cinema.enums.Type;
import jakarta.validation.constraints.*;
import org.hibernate.validator.constraints.Length;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CreateScreeningDto(
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
        @NotNull LocalDate date,
        @NotNull Type type,
        Long filmId,
        @Size(max = 100) String speakerName,
        @NotNull Long cinemaHallId
        ) {
}
