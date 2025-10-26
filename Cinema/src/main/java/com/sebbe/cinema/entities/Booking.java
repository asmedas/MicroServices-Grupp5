package com.sebbe.cinema.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.sebbe.cinema.enums.TechnicalEquipment;
import com.sebbe.cinema.enums.Type;
import jakarta.persistence.*;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "booking")
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "customer_id", nullable = false)
    @JsonBackReference
    private Customer customer;

    @Column(nullable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate date;

    @ManyToOne(optional = false)
    @JoinColumn(name = "cinemaHall_id", nullable = false)
    @JsonManagedReference
    private CinemaHall cinemaHall;

    @ManyToOne
    @JoinColumn(name = "film_id")
    @JsonManagedReference
    private Film film;

    @Column(name = "speaker", length = 100)
    private String speaker;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "booking_type", joinColumns = @JoinColumn(name = "booking_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private List<Type> type = new ArrayList<>();

    @Positive
    @Column(name = "guests", nullable = false)
    private int guests;

    @Positive
    @Column(name = "total_price_sek", nullable = false)
    private BigDecimal totalPriceSek;

    @Positive
    @Column(name = "total_price_usd", nullable = false)
    private BigDecimal totalPriceUsd;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "booking_equipment", joinColumns = @JoinColumn(name = "booking_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "technical_equipment")
    private List<TechnicalEquipment> technicalEquipment = new ArrayList<>();

    protected Booking() {}

    public Booking(Customer customer, CinemaHall cinemaHall, Film film, List<TechnicalEquipment> technicalEquipment, LocalDate date) {
        this.customer = customer;
        this.cinemaHall = cinemaHall;
        this.film = film;
        this.technicalEquipment = technicalEquipment;
        this.type = List.of(Type.FILM);
        this.guests = cinemaHall.getMaxSeats();
        this.createdAt = LocalDateTime.now();
        this.date = date;
    }

    public Booking(Customer customer, CinemaHall cinemaHall, String speaker, List<TechnicalEquipment> technicalEquipment, LocalDate date) {
        this.customer = customer;
        this.cinemaHall = cinemaHall;
        this.speaker = speaker;
        this.technicalEquipment = technicalEquipment;
        this.type = List.of(Type.FILM);
        this.guests = cinemaHall.getMaxSeats();
        this.createdAt = LocalDateTime.now();
        this.date = date;
    }

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }

    public Long getId() {
        return id;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public CinemaHall getCinemaHall() {
        return cinemaHall;
    }

    public void setCinemaHall(CinemaHall cinemaHall) {
        this.cinemaHall = cinemaHall;
    }

    public int getGuests() {
        return guests;
    }

    public void setGuests(int seats) {
        this.guests = seats;
    }

    public BigDecimal getTotalPriceSek() {
        return totalPriceSek;
    }

    public void setTotalPriceSek(BigDecimal totalPriceSek) {
        this.totalPriceSek = totalPriceSek;
    }

    public BigDecimal getTotalPriceUsd() {
        return totalPriceUsd;
    }

    public void setTotalPriceUsd(BigDecimal totalPriceUsd) {
        this.totalPriceUsd = totalPriceUsd;
    }

    public String getSpeaker() {
        return speaker;
    }

    public void setSpeaker(String speaker) {
        this.speaker = speaker;
    }

    public Film getFilm() {
        return film;
    }

    public void setFilm(Film film) {
        this.film = film;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public List<TechnicalEquipment> getTechnicalEquipment() {
        return technicalEquipment;
    }

    public void setTechnicalEquipment(List<TechnicalEquipment> technicalEquipment) {
        this.technicalEquipment = technicalEquipment;
    }

    public List<Type> getType() {
        return type;
    }

    public void setType(List<Type> type) {
        this.type = type;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public void removeBookingFromConnections(){
        if (customer != null) {
            customer.removeBooking(this);
            this.customer = null;
        }
        if (cinemaHall != null) {
            cinemaHall.removeBooking(this);
            this.cinemaHall = null;
        }
        if(film != null){
            film.removeBooking(this);
            this.film = null;
        }
    }
}
