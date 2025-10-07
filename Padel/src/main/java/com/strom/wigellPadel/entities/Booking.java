package com.strom.wigellPadel.entities;

import jakarta.persistence.*;

import java.sql.Time;
import java.util.Date;

@Entity
@Table(name = "bookings")
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    private Long customerId;
    private Long courtId;
    private double totalPrice;
    private int numberOfPlayers;
    private Date date;
    private Time time;

    protected Booking() {}

    public Booking(Long customerId, Long courtId, double totalPrice, int numberOfPlayers, Date date, Time time) {
        this.customerId = customerId;
        this.courtId = courtId;
        this.totalPrice = totalPrice;
        this.numberOfPlayers = numberOfPlayers;
        this.date = date;
        this.time = time;
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

    public double getTotalPrice() {
        return totalPrice;
    }

    public Booking setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
        return this;
    }

    public int getNumberOfPlayers() {
        return numberOfPlayers;
    }

    public Booking setNumberOfPlayers(int numberOfPlayers) {
        this.numberOfPlayers = numberOfPlayers;
        return this;
    }

    public Date getDate() {
        return date;
    }

    public Booking setDate(Date date) {
        this.date = date;
        return this;
    }

    public Time getTime() {
        return time;
    }

    public Booking setTime(Time time) {
        this.time = time;
        return this;
    }
}
