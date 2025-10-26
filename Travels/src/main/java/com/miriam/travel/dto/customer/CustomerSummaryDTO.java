package com.miriam.travel.dto.customer;

import com.miriam.travel.entity.Address;
import com.miriam.travel.entity.Booking;

import java.util.List;

public record CustomerSummaryDTO(
        String id,
        String username,
        String fullName,
        String email,
        List<Address> addresses,
        List<Booking> bookings
) {}
