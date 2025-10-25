package com.zetterlund.wigell_sushi_api.dto;

import java.time.LocalDateTime;

public class BookingResponseDto {
    private Integer bookingId;
    private String roomName;
    private LocalDateTime date;
    private int guestCount;
    private String catering;
    private boolean technicalEquipment;

    // Getters och setters
    public Integer getBookingId() {
        return bookingId;
    }
    public void setBookingId(Integer bookingId) {
        this.bookingId = bookingId;
    }
    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
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

    public String getCatering() {
        return catering;
    }
    public void setCatering(String catering) {
        this.catering = catering;
    }

    public boolean isTechnicalEquipment() {
        return technicalEquipment;
    }
    public void setTechnicalEquipment(boolean technicalEquipment) {
        this.technicalEquipment = technicalEquipment;
    }
}