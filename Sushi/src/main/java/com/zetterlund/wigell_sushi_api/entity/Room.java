package com.zetterlund.wigell_sushi_api.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "rooms")
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private int maxGuests;

    @Column(nullable = false)
    private boolean hasTechnicalEquipment;

    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Booking> bookings = new ArrayList<>();

    public Room() {}

    public Room(Integer id, String name, int maxGuests, boolean hasTechnicalEquipment, List<Booking> bookings) {
        this.id = id;
        this.name = name;
        this.maxGuests = maxGuests;
        this.hasTechnicalEquipment = hasTechnicalEquipment;
        this.bookings = bookings != null ? bookings : new ArrayList<>();
    }

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

    public List<Booking> getBookings() {
        return bookings;
    }
    public void setBookings(List<Booking> bookings) {
       this.bookings = bookings != null ? bookings : new ArrayList<>();
    }
}
