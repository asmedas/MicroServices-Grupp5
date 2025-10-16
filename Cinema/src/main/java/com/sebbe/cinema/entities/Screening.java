package com.sebbe.cinema.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.sebbe.cinema.enums.Type;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "screening")
public class Screening {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private BigDecimal priceSek;

    @Column(nullable = false)
    private BigDecimal priceUsd;

    @Column(nullable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate date;

    @ManyToOne
    @JoinColumn(name = "film_id")
    @JsonManagedReference
    private Film film;

    @Column(name = "speaker_name", length = 100)
    private String speakerName;

    @ManyToOne(optional = false)
    @JoinColumn(name = "cinemaHall_id", nullable = false)
    @JsonManagedReference
    private CinemaHall cinemaHall;

    @OneToMany(mappedBy = "screening",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    @JsonBackReference
    private List<Ticket> tickets = new ArrayList<>();

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "booking_type", joinColumns = @JoinColumn(name = "booking_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private List<Type> type = new ArrayList<>();

    protected Screening() {}

    public Screening(LocalDate date, Film film, CinemaHall cinemaHall, List<Type> type) {
        this.date = date;
        this.film = film;
        this.cinemaHall = cinemaHall;
        this.type = type;
    }

    public Screening(LocalDate date, String speakerName, CinemaHall cinemaHall, List<Type> type) {
        this.date = date;
        this.speakerName = speakerName;
        this.cinemaHall = cinemaHall;
        this.type = type;
    }

    public Long getId() {
        return id;
    }

    public BigDecimal getPriceSek() {
        return priceSek;
    }

    public void setPriceSek(BigDecimal priceSek) {
        this.priceSek = priceSek;
    }

    public BigDecimal getPriceUsd() {
        return priceUsd;
    }

    public void setPriceUsd(BigDecimal priceUsd) {
        this.priceUsd = priceUsd;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Film getFilm() {
        return film;
    }

    public void setFilm(Film film) {
        this.film = film;
    }

    public String getSpeakerName() {
        return speakerName;
    }

    public void setSpeakerName(String speakerName) {
        this.speakerName = speakerName;
    }

    public CinemaHall getCinemaHall() {
        return cinemaHall;
    }

    public void setCinemaHall(CinemaHall cinemaHall) {
        this.cinemaHall = cinemaHall;
    }

    public List<Ticket> getTickets() {
        return tickets;
    }

    public void setTickets(List<Ticket> tickets) {
        this.tickets = tickets;
    }

    public void removeTicket(Ticket ticket){
        this.tickets.remove(ticket);
    }

    public void addTicket(Ticket ticket){this.tickets.add(ticket);}

    public List<Type> getType() {
        return type;
    }

    public void setType(List<Type> type) {
        this.type = type;
    }

    public void removeScreeningFromConnections(){
        if (film != null) {
            film.removeScreening(this);
            this.film = null;
        }
        if (cinemaHall != null) {
            cinemaHall.removeScreening(this);
            this.cinemaHall = null;
        }
    }


}
