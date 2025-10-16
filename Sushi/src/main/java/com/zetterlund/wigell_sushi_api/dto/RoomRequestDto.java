package com.zetterlund.wigell_sushi_api.dto;

// För skapande/uppdatering av nya rum
public class RoomRequestDto {
    private String name;
    private int maxGuests;
    private boolean hasTechnicalEquipment;

    // Getters och setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getMaxGuests() {
        return maxGuests;
    }

    public void setMaxGuests(int maxGuests) {
        this.maxGuests = maxGuests;
    }

    public boolean isHasTechnicalEquipment() {
        return hasTechnicalEquipment;
    }

    public void setHasTechnicalEquipment(boolean hasTechnicalEquipment) {
        this.hasTechnicalEquipment = hasTechnicalEquipment;
    }
}
