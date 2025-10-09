package com.sebbe.cinema.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "customer")
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

//    @Column(nullable = false, unique = true)
//    private String keycloakId;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String name;

    @Positive
    @Column(nullable = false)
    @Max(120)
    private int age;

    @ManyToMany
    @JoinTable(
            name = "customer_address",
            joinColumns = @JoinColumn(name = "customer_id"),
            inverseJoinColumns = @JoinColumn(name = "address_id")
    )
    Set<Address> addresses = new HashSet<>();

    @OneToMany(mappedBy = "customer", cascade = {CascadeType.MERGE, CascadeType.PERSIST}, orphanRemoval = true,
    fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<Ticket> tickets;

    @OneToMany(mappedBy = "customer", cascade = {CascadeType.MERGE, CascadeType.PERSIST}, orphanRemoval = true,
    fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<Booking> bookings;

    protected Customer() {}

    public Customer(String username, String name, int age, Set<Address> address) {
        this.username = username;
        this.name = name;
        this.age = age;
        this.addresses = address;
    }

    public Customer(String username, String name, int age, Set<Address> address,
                    List<Ticket> tickets, List<Booking> bookings) {
        this.username = username;
        this.name = name;
        this.age = age;
        this.addresses = address;
        this.tickets = tickets;
        this.bookings = bookings;
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public Set<Address> getAddress() {
        return addresses;
    }

    public void setAddress(Set<Address> address) {
        this.addresses = address;
    }

    public void addAddress(Address address) {
        addresses.add(address);
        address.getCustomers().add(this);
    }
    public void removeAddress(Address address) {
        addresses.remove(address);
        address.getCustomers().remove(this);
    }

    public List<Ticket> getTickets() {
        return tickets;
    }

    public void setTickets(List<Ticket> tickets) {
        this.tickets = tickets;
    }

    public List<Booking> getBookings() {
        return bookings;
    }

    public void setBookings(List<Booking> bookings) {
        this.bookings = bookings;
    }

    public void addTicket(Ticket ticket) {
        tickets.add(ticket);
        ticket.setCustomer(this);
    }
    public void removeTicket(Ticket ticket) {
        tickets.remove(ticket);
        ticket.setCustomer(null);
    }

    @Override
    public String toString() {
        return "Customer{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", age=" + age +
                '}';
    }
}
