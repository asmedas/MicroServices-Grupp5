package com.miriam.travel.dto.booking;

import jakarta.validation.constraints.Min;

public class BookingPatchRequest {
    public Long destinationId;
    public String hotelName;
    @Min(1) public Integer weeks;
}
