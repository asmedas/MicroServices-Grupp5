package com.miriam.travel.dto.customer;

public record CustomerSummaryDTO(
        String id,
        String username,
        String fullName,
        String email,
        int addressCount,
        int bookingCount
) {}
