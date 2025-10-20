package com.sebbe.cinema.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "film")
public class Film {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Positive
    @Max(18)
    private Integer ageLimit;

    @Column(nullable = false, length = 250)
    private String title;

    @Column(nullable = false, length = 100)
    private String genre;

    @Positive
    @Min(1)
    @Max(300)
    @Column(nullable = false)
    private Integer length;

    @OneToMany(mappedBy = "film", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonBackReference
    private List<Screening> screenings = new ArrayList<>();

    @OneToMany(mappedBy = "film", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonBackReference
    private List<Booking> bookings = new ArrayList<>();


    protected Film() {}

    public Film(String title, String genre, int length){
        this.title = title;
        this.genre = genre;
        this.length = length;
    }

    public Film(int ageLimit, String title, String genre, int length) {
        this.ageLimit = ageLimit;
        this.title = title;
        this.genre = genre;
        this.length = length;
    }

    public Long getId() {
        return id;
    }

    public Integer getAgeLimit() {
        return ageLimit;
    }

    public void setAgeLimit(Integer ageLimit) {
        this.ageLimit = ageLimit;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public List<Screening> getScreenings() {
        return screenings;
    }

    public void setScreenings(List<Screening> screenings) {
        this.screenings = screenings;
    }

    public void removeScreening(Screening screening){
        screenings.remove(screening);
    }

    public List<Booking> getBookings() {
        return bookings;
    }

    public void setBookings(List<Booking> bookings) {
        this.bookings = bookings;
    }

    public void removeBooking(Booking booking){bookings.remove(booking);}

    @Override
    public String toString() {
        return "Film{" +
                "id=" + id +
                ", ageLimit=" + ageLimit +
                ", title='" + title + '\'' +
                ", genre='" + genre + '\'' +
                '}';
    }
}
