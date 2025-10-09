package com.sebbe.cinema.controllers;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/rooms")
public class CinemaHallController {

    @GetMapping
    public String listRooms() {
        return "GET /api/v1/rooms";
    }

    @GetMapping("/{roomId}")
    public String getRoom(@PathVariable Long roomId) {
        return "GET /api/v1/rooms/" + roomId;
    }

    @PostMapping
    public String addRoom() {
        return "POST /api/v1/rooms";
    }

    @PutMapping("/{roomId}")
    public String updateRoom(@PathVariable Long roomId) {
        return "PUT /api/v1/rooms/" + roomId;
    }

}
