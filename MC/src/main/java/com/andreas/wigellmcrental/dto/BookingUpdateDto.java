package com.andreas.wigellmcrental.dto;

import com.andreas.wigellmcrental.entity.BookingStatus;
import java.time.LocalDate;

/**
 * Allt valfritt. Används för PATCH.
 * Kund får skicka bikeId/startDate/endDate.
 * Admin får även skicka status.
 */
public record BookingUpdateDto(
        Long bikeId,
        LocalDate startDate,
        LocalDate endDate,
        BookingStatus status
) {}
