package com.strom.wigellPadel.dto;

import java.sql.Time;
import java.util.Date;

public record BookingDto (
        Long customerId,
        Long courtId,
        double price,
        int numberOfPlayers,
        Date date,
        Time time
){
}
