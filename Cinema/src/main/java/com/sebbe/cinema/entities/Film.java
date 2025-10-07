package com.sebbe.cinema.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "film")
public class Film {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int ageLimit;
    private String title;
    private String genre;


    protected Film() {}

    public Film(int ageLimit, String title, String genre) {
        this.ageLimit = ageLimit;
        this.title = title;
        this.genre = genre;
    }

    public Long getId() {
        return id;
    }

    public int getAgeLimit() {
        return ageLimit;
    }

    public void setAgeLimit(int ageLimit) {
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
