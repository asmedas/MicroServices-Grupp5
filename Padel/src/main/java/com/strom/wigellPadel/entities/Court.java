package com.strom.wigellPadel.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "courts")
public class Court {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "information", nullable = false, length = 100)
    private String information;

    @Column(name = "price", nullable = false)
    private double price;

    protected Court() {}

    public Court(String information, double price) {
        this.information = information;
        this.price = price;
    }

    public Long getId() {
        return id;
    }

    public Court setId(Long id) {
        this.id = id;
        return this;
    }

    public String getInformation() {
        return information;
    }

    public Court setInformation(String information) {
        this.information = information;
        return this;
    }

    public double getPrice() {
        return price;
    }

    public Court setPrice(double price) {
        this.price = price;
        return this;
    }

}
