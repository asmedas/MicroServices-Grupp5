package com.strom.wigellPadel.mapper;

import com.strom.wigellPadel.dto.CourtCreateDto;
import com.strom.wigellPadel.dto.CourtDto;
import com.strom.wigellPadel.entities.Court;

public class CourtMapper {

    public CourtMapper() {
    }

    public static CourtDto toDto(Court court, double priceInEUR) {
        if (court == null) {
            return null;
        }

        return new CourtDto(
                court.getId(),
                court.getInformation(),
                court.getPrice(),
                priceInEUR
        );
    }

    public static Court fromCreate (CourtCreateDto dto) {
        if (dto == null) {
            return null;
        }
        Court newCourt = new Court(dto.information(), dto.price());
        return newCourt;
    }

}
