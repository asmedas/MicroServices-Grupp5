package com.zetterlund.wigell_sushi_api.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "addresses")
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    @NotBlank(message = "Street cannot be blank.")
    private String street;

    @Column(nullable = false)
    @NotBlank(message = "Postal code cannot be blank.")
    private String postalCode;

    @Column(nullable = false)
    @NotBlank(message = "City cannot be blank.")
    private String city;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    public Address() {}

    public Address(String street, String postalCode, String city, Customer customer) {
        this.street = street;
        this.postalCode = postalCode;
        this.city = city;
        this.customer = customer;
    }

    // Getters och setters
    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }

    public String getStreet() {
        return street;
    }
    public void setStreet(String street) {
        this.street = street;
    }

    public String getPostalCode() {
        return postalCode;
    }
    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getCity() {
        return city;
    }
    public void setCity(String city) {
        this.city = city;
    }

    public Customer getCustomer() {
        return customer;
    }
    public void setCustomer(Customer customer) {
        this.customer = customer;
    }
}
