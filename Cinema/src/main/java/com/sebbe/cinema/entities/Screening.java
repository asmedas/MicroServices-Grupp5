package com.sebbe.cinema.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.sebbe.cinema.enums.Type;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "screening")
public class Screening {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private BigDecimal price;

    @Column(nullable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate date;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "screening_type", joinColumns = @JoinColumn(name = "screening_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private List<Type> type;

    @ManyToOne
    @JoinColumn(name = "film_id")
    @JsonBackReference
    private Film film;

    @Column(name = "speaker_name", length = 100)
    private String speakerName;

    @ManyToOne
    @JoinColumn(name = "cinemaHall_id", nullable = false)
    @JsonBackReference
    private CinemaHall cinemaHall;

    @OneToOne(mappedBy = "screening", optional = true)
    @JsonBackReference
    private Booking booking;

    @OneToMany(mappedBy = "screening", cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    @JsonManagedReference
    private List<Ticket> tickets;

    protected Screening() {}

    public Screening(BigDecimal price, LocalDate date, List<Type> type, Film film, CinemaHall cinemaHall) {
        this.price = price;
        this.date = date;
        this.type = type;
        this.film = film;
        this.cinemaHall = cinemaHall;
    }

    public Long getId() {
        return id;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public List<Type> getType() {
        return type;
    }

    public void setType(List<Type> type) {
        this.type = type;
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

    public Booking getBooking() {
        return booking;
    }

    public void setBooking(Booking bookings) {
        this.booking = bookings;
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

    public void removeBooking(Booking booking){
        this.booking = null;
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
