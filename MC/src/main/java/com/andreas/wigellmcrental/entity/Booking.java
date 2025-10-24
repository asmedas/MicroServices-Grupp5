package com.andreas.wigellmcrental.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
public class Booking {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional=false)
    private Customer customer;

    @ManyToOne(optional=false)
    private Bike bike;

    private LocalDate startDate;
    private LocalDate endDate;

    private double totalPriceSek;
    private double totalPriceGbp;

    @Enumerated(EnumType.STRING)
    private BookingStatus status = BookingStatus.ACTIVE; // gör den till ACTIVE by default

    public Booking() {
    }

    public Booking(Long id, Customer customer, Bike bike, LocalDate startDate, LocalDate endDate, double totalPriceSek, double totalPriceGbp, BookingStatus status) {
        this.id = id;
        this.customer = customer;
        this.bike = bike;
        this.startDate = startDate;
        this.endDate = endDate;
        this.totalPriceSek = totalPriceSek;
        this.totalPriceGbp = totalPriceGbp;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Bike getBike() {
        return bike;
    }

    public void setBike(Bike bike) {
        this.bike = bike;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public double getTotalPriceSek() {
        return totalPriceSek;
    }

    public void setTotalPriceSek(double totalPriceSek) {
        this.totalPriceSek = totalPriceSek;
    }

    public double getTotalPriceGbp() {
        return totalPriceGbp;
    }

    public void setTotalPriceGbp(double totalPriceGbp) {
        this.totalPriceGbp = totalPriceGbp;
    }

    public BookingStatus getStatus() {
        return status;
    }

    public void setStatus(BookingStatus status) {
        this.status = status;
    }
}

