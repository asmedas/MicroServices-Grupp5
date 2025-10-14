package com.sebbe.cinema.entities;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Positive;

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

    @OneToMany(mappedBy = "film", cascade = {CascadeType.MERGE, CascadeType.PERSIST}, fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<Screening> screenings;


    protected Film() {}

    public Film(int ageLimit, String title, String genre) {
        this.ageLimit = ageLimit;
        this.title = title;
        this.genre = genre;
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

    public List<Screening> getScreenings() {
        return screenings;
    }

    public void setScreenings(List<Screening> screenings) {
        this.screenings = screenings;
    }

    public void removeScreening(Screening screening){
        screenings.remove(screening);
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
