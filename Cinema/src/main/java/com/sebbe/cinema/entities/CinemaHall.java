package com.sebbe.cinema.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "cinema_halls")
public class CinemaHall {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int maxSeats;



}
