package com.strom.wigellPadel.entities;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "address")
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "street", nullable = false, length = 50)
    private String street;

    @Column(name = "postal_code", nullable = false, length = 50)
    private String postalCode;

    @Column(name = "city", nullable = false, length = 50)
    private String city;

    @JsonManagedReference
    @OneToMany(mappedBy = "address", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = false)
    private List<Customer> customers = new ArrayList<>();

    protected Address() {}

    public Address(String street, String postalCode, String city, List<Customer> customers) {
        this.street = street;
        this.postalCode = postalCode;
        this.city = city;
        this.customers = customers;
    }

    public Long getId() {
        return id;
    }

    public Address setId(Long id) {
        this.id = id;
        return this;
    }

    public String getStreet() {
        return street;
    }

    public Address setStreet(String street) {
        this.street = street;
        return this;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public Address setPostalCode(String postalCode) {
        this.postalCode = postalCode;
        return this;
    }

    public String getCity() {
        return city;
    }

    public Address setCity(String city) {
        this.city = city;
        return this;
    }

    public List<Customer> getCustomers() {
        return customers;
    }

    public Address setCustomers(List<Customer> customers) {
        this.customers = customers;
        return this;
    }
}
