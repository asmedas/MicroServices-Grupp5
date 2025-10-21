package com.sebbe.cinema.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

@Entity
@Table(name = "ticket")
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "customer_id", nullable = false)
    @JsonBackReference
    private Customer customer;

    @ManyToOne(optional = false)
    @JoinColumn(name = "screening_id", nullable = false)
    @JsonManagedReference
    private Screening screening;

    @Positive
    @Column(name = "price_sek", nullable = false)
    private BigDecimal priceSek;

    @Positive
    @Column(name = "price_usd", nullable = false)
    private BigDecimal priceUsd;

    protected Ticket() {}

    public Ticket(Screening screening, Customer customer) {
        this.screening = screening;
        this.customer = customer;
    }
    public Long getId() {
        return id;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setScreening(Screening screening) {
        this.screening = screening;
    }

    public Screening getScreening() {
        return screening;
    }

    public void setPriceSek(BigDecimal priceSek) {
        this.priceSek = priceSek;
    }

    public BigDecimal getPriceSek() {
        return priceSek;
    }

    public void setPriceUsd(BigDecimal priceUsd) {
        this.priceUsd = priceUsd;
    }

    public BigDecimal getPriceUsd() {
        return priceUsd;
    }

    public void removeTicketFromConnections(){
        if (customer != null) {
            customer.removeTicket(this);
            this.customer = null;
        }
        if (screening != null) {
            screening.removeTicket(this);
            this.screening = null;
        }
    }
}
