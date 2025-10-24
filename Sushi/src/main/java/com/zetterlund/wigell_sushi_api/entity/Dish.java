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
    private BigDecimal priceInSek;

    @Column(nullable = false)
    private BigDecimal priceInJpy;

    @Column
    private String description;

    public Dish() {}

    public Dish(Integer id, String name, BigDecimal priceInSek, BigDecimal priceInJpy, String description) {
        this.id = id;
        this.name = name;
        if (priceInSek != null) this.priceInSek = BigDecimal.valueOf(priceInSek.doubleValue());
        if (priceInJpy != null) this.priceInJpy = BigDecimal.valueOf(priceInJpy.doubleValue());
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

    public BigDecimal getPriceInSek() {
        return priceInSek;
    }
    public void setPriceInSek(BigDecimal priceInSek) {
        this.priceInSek = priceInSek;
    }

    public BigDecimal getPriceInJpy() {
        return priceInJpy;
    }
    public void setPriceInJpy(BigDecimal priceInJpy) {
        this.priceInJpy = priceInJpy;
    }

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
}
