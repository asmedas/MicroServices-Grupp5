package com.zetterlund.wigell_sushi_api.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "bookings")
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private LocalDateTime date;

    @ManyToOne
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;

    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @Column(nullable = false)
    private int guestCount;

    @OneToMany(mappedBy = "booking", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BookingDetails> bookingDetails = new ArrayList<>();

    public Booking() {}

    public Booking(Integer id, LocalDateTime date, Room room, Customer customer, int guestCount) {
        this.id = id;
        this.date = date;
        this.room = room;
        this.customer = customer;
        this.guestCount = guestCount;
    }

    // Getters och setters
    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }

    public LocalDateTime getDate() {
        return date;
    }
    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public Room getRoom() {
        return room;
    }
    public void setRoom(Room room) {
        this.room = room;
    }

    public Customer getCustomer() {
        return customer;
    }
    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public int getGuestCount() {
        return guestCount;
    }
    public void setGuestCount(int guestCount) {
        this.guestCount = guestCount;
    }

    public List<BookingDetails> getBookingDetails() {
        return bookingDetails;
    }
    public void setBookingDetails(List<BookingDetails> bookingDetails) {
        this.bookingDetails = bookingDetails;
    }
}
