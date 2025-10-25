package com.miriam.travel.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

@Entity
@Table(name = "bookings")
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable=false)
    private LocalDate departureDate;

    @Column(nullable=false)
    private int weeks;

    @Column(nullable=false, length=128)
    private String hotelName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    @JsonBackReference(value = "customer-booking")
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "destination_id", nullable = false)
    @JsonBackReference(value = "destination-booking")
    private Destination destination;

    @Column(nullable=false, precision = 14, scale = 2)
    private BigDecimal totalPriceSek;

    @Column(nullable=false, precision = 14, scale = 2)
    private BigDecimal totalPricePln;

    public Booking() {}

    public Booking(LocalDate departureDate, int weeks, String hotelName,
                   Customer customer, Destination destination,
                   BigDecimal totalPriceSek, BigDecimal totalPricePln) {
        this.departureDate = departureDate;
        this.weeks = weeks;
        this.hotelName = hotelName;
        this.customer = customer;
        this.destination = destination;
        this.totalPriceSek = totalPriceSek;
        this.totalPricePln = totalPricePln;
    }

    // getters & setters
    public Long getId() { return id; }

    public LocalDate getDepartureDate() { return departureDate; }
    public void setDepartureDate(LocalDate departureDate) { this.departureDate = departureDate; }

    public int getWeeks() { return weeks; }
    public void setWeeks(int weeks) { this.weeks = weeks; }

    public String getHotelName() { return hotelName; }
    public void setHotelName(String hotelName) { this.hotelName = hotelName; }

    public Customer getCustomer() { return customer; }
    public void setCustomer(Customer customer) { this.customer = customer; }

    public Destination getDestination() { return destination; }
    public void setDestination(Destination destination) { this.destination = destination; }

    public BigDecimal getTotalPriceSek() { return totalPriceSek; }
    public void setTotalPriceSek(BigDecimal totalPriceSek) { this.totalPriceSek = totalPriceSek; }

    public BigDecimal getTotalPricePln() { return totalPricePln; }
    public void setTotalPricePln(BigDecimal totalPricePln) { this.totalPricePln = totalPricePln; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Booking)) return false;
        Booking booking = (Booking) o;
        return Objects.equals(id, booking.id);
    }

    @Override
    public int hashCode() { return Objects.hash(id); }
}
