package com.zetterlund.wigell_sushi_api.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private double totalPriceInSek;

    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @ManyToMany
    @JoinTable(
            name = "order_dishes",
            joinColumns = @JoinColumn(name = "order_id"),
            inverseJoinColumns = @JoinColumn(name = "dish_id")
    )
    private List<Dish> dishes = new ArrayList<>();

    // Getters och setters
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public double getTotalPriceInSek() {
        return totalPriceInSek;
    }
    public void setTotalPriceInSek(double totalPriceInSek) {
        this.totalPriceInSek = totalPriceInSek;
    }

    public Customer getCustomer() {
        return customer;
    }
    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public List<Dish> getDishes() {
        return dishes;
    }
    public void setDishes(List<Dish> dishes) {
        this.dishes = dishes;
    }
}
