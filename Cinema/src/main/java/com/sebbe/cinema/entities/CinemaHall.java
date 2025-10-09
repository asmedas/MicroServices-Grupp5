package com.sebbe.cinema.entities;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.sebbe.cinema.enums.TechnicalEquipment;
import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "cinema_hall")
public class CinemaHall {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private int maxSeats;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "cinema_hall_equipment", joinColumns = @JoinColumn(name = "cinema_hall_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "technical_equipment")
    private List<TechnicalEquipment> technicalEquipment;

    @OneToMany(mappedBy = "cinemaHall", cascade = {CascadeType.MERGE, CascadeType.PERSIST}, fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<Screening> screenings;

    protected CinemaHall() {}

    public CinemaHall(String name, int maxSeats) {
        this.name = name;
        this.maxSeats = maxSeats;
    }

    public CinemaHall(String name, int maxSeats, List<TechnicalEquipment> technicalEquipment, List<Screening> screenings) {
        this.name = name;
        this.maxSeats = maxSeats;
        this.technicalEquipment = technicalEquipment;
        this.screenings = screenings;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getMaxSeats() {
        return maxSeats;
    }

    public void setMaxSeats(int maxSeats) {
        this.maxSeats = maxSeats;
    }

    public List<TechnicalEquipment> getTechnicalEquipment() {
        return technicalEquipment;
    }

    public void setTechnicalEquipment(List<TechnicalEquipment> technicalEquipment) {
        this.technicalEquipment = technicalEquipment;
    }

    public List<Screening> getScreenings() {
        return screenings;
    }

    public void setScreenings(List<Screening> screenings) {
        this.screenings = screenings;
    }
}
