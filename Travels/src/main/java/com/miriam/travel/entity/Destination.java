package com.miriam.travel.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "destinations")
public class Destination {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable=false, length=128)
    private String hotelName;

    @Column(nullable=false, length=64)
    private String city;

    @Column(nullable=false, length=64)
    private String country;

    @Column(nullable=false, precision = 12, scale = 2)
    private BigDecimal pricePerWeekSek;

    @OneToMany(mappedBy = "destination", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference(value = "destination-booking")
    private List<Booking> bookings = new ArrayList<>();

    public Destination() {}

    public Destination(String hotelName, String city, String country, BigDecimal pricePerWeekSek) {
        this.hotelName = hotelName;
        this.city = city;
        this.country = country;
        this.pricePerWeekSek = pricePerWeekSek;
    }

    // getters & setters
    public Long getId() { return id; }

    public String getHotelName() { return hotelName; }
    public void setHotelName(String hotelName) { this.hotelName = hotelName; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }

    public BigDecimal getPricePerWeekSek() { return pricePerWeekSek; }
    public void setPricePerWeekSek(BigDecimal pricePerWeekSek) { this.pricePerWeekSek = pricePerWeekSek; }

    public List<Booking> getBookings() { return bookings; }
    public void setBookings(List<Booking> bookings) { this.bookings = bookings; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Destination)) return false;
        Destination that = (Destination) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() { return Objects.hash(id); }
}
