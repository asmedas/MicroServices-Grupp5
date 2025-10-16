package com.zetterlund.wigell_sushi_api.dto;

import java.time.LocalDateTime;

public class BookingRequestDto {
    private Integer roomId;
    private Integer customerId;
    private LocalDateTime date;
    private int guestCount;

    // Getters och setters
    public Integer getRoomId() {
        return roomId;
    }
    public void setRoomId(Integer roomId) {
        this.roomId = roomId;
    }

    public Integer getCustomerId() {
        return customerId;
    }
    public void setCustomerId(Integer customerId) {
        this.customerId = customerId;
    }

    public LocalDateTime getDate() {
        return date;
    }
    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public int getGuestCount() {
        return guestCount;
    }
    public void setGuestCount(int guestCount) {
        this.guestCount = guestCount;
    }
}
