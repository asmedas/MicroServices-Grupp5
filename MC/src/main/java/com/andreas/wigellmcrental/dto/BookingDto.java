package com.andreas.wigellmcrental.dto;


import com.andreas.wigellmcrental.entity.BookingStatus;
import java.time.LocalDate;

public record BookingDto(
        Long id,
        Long customerId,
        Long bikeId,
        String bikeModel,
        LocalDate startDate,
        LocalDate endDate,
        double totalPriceSek,
        double totalPriceGbp,
        BookingStatus status
) {}
