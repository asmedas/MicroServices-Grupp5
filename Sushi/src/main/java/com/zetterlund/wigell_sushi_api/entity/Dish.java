package com.zetterlund.wigell_sushi_api.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "dishes")
public class Dish {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false)
    private Double priceInSek;

    @Column(nullable = false)
    private Double priceInJpy;

    @Column
    private String description;

    public Dish() {}

    public Dish(Integer id, String name, BigDecimal priceInSek, BigDecimal PriceInJpy, String description) {
        this.name = name;
        this.description = description;
    }

    // Getters och setters
    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public Double getPriceInSek() {
        return priceInSek;
    }
    public void setPriceInSek(Double priceInSek) {
        this.priceInSek = priceInSek;
    }

    public Double getPriceInJpy() {
        return priceInJpy;
    }
    public void setPriceInJpy(Double priceInJpy) {
        this.priceInJpy = priceInJpy;
    }

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
}
