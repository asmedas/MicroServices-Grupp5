package com.strom.wigellPadel.mapper;

import com.strom.wigellPadel.dto.BookingCreateDto;
import com.strom.wigellPadel.dto.BookingDto;
import com.strom.wigellPadel.dto.CourtDto;
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
                booking.getNumberOfPlayers(),
                booking.getDate(),
                booking.getTimeSlot(),
                booking.getTotalPrice()
        );
    }

    public static Booking fromCreate (BookingCreateDto dto) {
        if (dto == null) {
            return null;
        }
        Booking newBooking = new Booking(dto.customerId(), dto.courtId(), dto.numberOfPlayers(), dto.date(), dto.timeSlot());
        return newBooking;
    }

}
