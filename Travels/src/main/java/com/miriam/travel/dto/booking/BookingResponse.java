package com.miriam.travel.dto.booking;

import java.math.BigDecimal;
import java.time.LocalDate;

public record BookingResponse(
        Long id,
        String customerId,
        Long destinationId,
        String destinationCity,
        String destinationCountry,
        String hotelName,
        LocalDate departureDate,
        int weeks,
        BigDecimal totalPriceSek,
        BigDecimal totalPricePln
) {}
