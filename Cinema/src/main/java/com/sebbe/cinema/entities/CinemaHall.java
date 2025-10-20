package com.sebbe.cinema.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.sebbe.cinema.enums.TechnicalEquipment;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "cinema_hall")
public class CinemaHall {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String name;

    @Column(nullable = false)
    @Positive
    @Max(5000)
    private Integer maxSeats;

    @OneToMany(mappedBy = "cinemaHall",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    @JsonBackReference
    private List<Screening> screenings = new ArrayList<>();

    @OneToMany(mappedBy = "cinemaHall",
        cascade = CascadeType.ALL,
        orphanRemoval = true)
    private List<Booking> bookings = new ArrayList<>();

    protected CinemaHall() {}

    public CinemaHall(String name, Integer maxSeats) {
        this.name = name;
        this.maxSeats = maxSeats;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getMaxSeats() {
        return maxSeats;
    }

    public void setMaxSeats(Integer maxSeats) {
        this.maxSeats = maxSeats;
    }

    public List<Screening> getScreenings() {
        return screenings;
    }

    public void setScreenings(List<Screening> screenings) {
        this.screenings = screenings;
    }

    public List<Booking> getBookings() {
        return bookings;
    }

    public void setBookings(List<Booking> bookings) {
        this.bookings = bookings;
    }

    public void addScreening(Screening screening) {
        screenings.add(screening);
    }

    public void removeScreening(Screening screening) {
        screenings.remove(screening);
    }

    public void addBooking(Booking booking) {
        bookings.add(booking);
    }

    public void removeBooking(Booking booking) {
        bookings.remove(booking);
    }
}
