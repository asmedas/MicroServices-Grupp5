package com.zetterlund.wigell_sushi_api.dto;

// För utdata, returnerar bara viktig info om rummet
public class RoomResponseDto {
    private Integer id;
    private String name;
    private int maxGuests;
    private boolean hasTechnicalEquipment;

    // Getters och setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

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
