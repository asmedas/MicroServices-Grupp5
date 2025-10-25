package com.zetterlund.wigell_sushi_api.dto;

import java.time.LocalDateTime;

public class BookingRequestDto {
    private Integer roomId;
    private Integer customerId;
    private LocalDateTime date;
    private Integer guestCount;
    private String catering; // önskad förtäring
    private Boolean technicalEquipment;

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

    public Integer getGuestCount() {
        return guestCount;
    }
    public void setGuestCount(Integer guestCount) {
        this.guestCount = guestCount;
    }

    public String getCatering() {
        return catering;
    }
    public void setCatering(String catering) {
        this.catering = catering;
    }

    public Boolean getTechnicalEquipment() { return technicalEquipment; }
    public void setTechnicalEquipment(Boolean technicalEquipment) { this.technicalEquipment = technicalEquipment; }
}
