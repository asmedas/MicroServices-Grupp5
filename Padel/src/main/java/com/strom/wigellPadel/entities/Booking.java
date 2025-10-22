package com.strom.wigellPadel.entities;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalTime;


@Entity
@Table(name = "bookings")
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    private Long customerId;
    private Long courtId;
    private int numberOfPlayers;
    private LocalDate date;
    private int timeSlot;
    private double totalPrice;

    protected Booking() {}

    public Booking(Long customerId, Long courtId, int numberOfPlayers, LocalDate date, int timeSlot) {
        this.customerId = customerId;
        this.courtId = courtId;
        this.numberOfPlayers = numberOfPlayers;
        this.date = date;
        this.timeSlot = timeSlot;
    }

    public Booking(Long customerId, Long courtId, int numberOfPlayers, LocalDate date, int timeSlot, double totalPrice) {
        this.customerId = customerId;
        this.courtId = courtId;
        this.numberOfPlayers = numberOfPlayers;
        this.date = date;
        this.timeSlot = timeSlot;
        this.totalPrice = totalPrice;
    }

    public Long getId() {
        return id;
    }

    public Booking setId(Long id) {
        this.id = id;
        return this;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public Booking setCustomerId(Long customerId) {
        this.customerId = customerId;
        return this;
    }

    public Long getCourtId() {
        return courtId;
    }

    public Booking setCourtId(Long courtId) {
        this.courtId = courtId;
        return this;
    }

    public int getNumberOfPlayers() {
        return numberOfPlayers;
    }

    public Booking setNumberOfPlayers(int numberOfPlayers) {
        this.numberOfPlayers = numberOfPlayers;
        return this;
    }

    public LocalDate getDate() {
        return date;
    }

    public Booking setDate(LocalDate date) {
        this.date = date;
        return this;
    }

    public int getTimeSlot() {
        return timeSlot;
    }

    public Booking setTimeSlot(int timeSlot) {
        this.timeSlot = timeSlot;
        return this;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public Booking setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
        return this;
    }
}
