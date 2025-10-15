package com.strom.wigellPadel.mapper;

import com.strom.wigellPadel.dto.BookingDto;
import com.strom.wigellPadel.entities.Booking;

public class BookingMapper {

    public BookingMapper() {
    }

    public static BookingDto toDto (Booking booking) {
        if (booking == null) {
            return null;
        }

        return new BookingDto(
                booking.getCustomerId(),
                booking.getCourtId(),
                booking.getTotalPrice(),
                booking.getNumberOfPlayers(),
                booking.getDate(),
                booking.getTime()
        );
    }
}
