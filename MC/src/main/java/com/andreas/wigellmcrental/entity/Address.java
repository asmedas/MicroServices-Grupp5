package com.andreas.wigellmcrental.entity;

import jakarta.persistence.*;

@Entity
public class Address {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String street;
    private String city;
    private String country;
    private String postalCode;

    @ManyToOne(optional=false)
    private Customer customer;

    public Address() {
    }
    public Address(Long id, String street, String city, String country, String postalCode) {
        this.id = id;
        this.street = street;
        this.city = city;
        this.country = country;
        this.postalCode = postalCode;
    }

    public Address(String city, String street, String postalCode, String country) {
        this.city = city;
        this.street = street;
        this.postalCode = postalCode;
        this.country = country;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }
}

