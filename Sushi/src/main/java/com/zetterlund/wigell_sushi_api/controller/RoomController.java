package com.zetterlund.wigell_sushi_api.controller;

import com.zetterlund.wigell_sushi_api.dto.RoomRequestDto;
import com.zetterlund.wigell_sushi_api.dto.RoomResponseDto;
import com.zetterlund.wigell_sushi_api.entity.Room;
import com.zetterlund.wigell_sushi_api.service.RoomService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/rooms")
public class RoomController {
    private final RoomService roomService;

    public RoomController(RoomService roomService) {
        this.roomService = roomService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<RoomResponseDto>> getAllRooms() {
        List<Room> rooms = roomService.getAllRooms();

        List<RoomResponseDto> roomDto = rooms.stream().map(room -> {
            RoomResponseDto dto = new RoomResponseDto();
            dto.setId(room.getId());
            dto.setName(room.getName());
            dto.setMaxGuests(room.getMaxGuests());
            dto.setHasTechnicalEquipment(room.isHasTechnicalEquipment());
            return dto;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(roomDto);
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<RoomResponseDto> addRoom(@RequestBody RoomRequestDto roomDto) {
        Room room = new Room();
        room.setName(roomDto.getName());
        room.setMaxGuests(roomDto.getMaxGuests());
        room.setHasTechnicalEquipment(roomDto.isHasTechnicalEquipment());

        Room createdRoom = roomService.addRoom(room);

        RoomResponseDto responseDto = new RoomResponseDto();
        responseDto.setId(createdRoom.getId());
        responseDto.setName(createdRoom.getName());
        responseDto.setMaxGuests(createdRoom.getMaxGuests());
        responseDto.setHasTechnicalEquipment(createdRoom.isHasTechnicalEquipment());

        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<RoomResponseDto> getRoomById(@PathVariable Integer id) {
        Room room = roomService.getRoomById(id);

        RoomResponseDto dto = new RoomResponseDto();
        dto.setId(room.getId());
        dto.setName(room.getName());
        dto.setMaxGuests(room.getMaxGuests());
        dto.setHasTechnicalEquipment(room.isHasTechnicalEquipment());

        return ResponseEntity.ok(dto);
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<RoomResponseDto> updateRoom(
            @PathVariable Integer id,
            @RequestBody RoomRequestDto roomDto) {

        Room room = new Room();
        room.setName(roomDto.getName());
        room.setMaxGuests(roomDto.getMaxGuests());
        room.setHasTechnicalEquipment(roomDto.isHasTechnicalEquipment());

        Room updatedRoom = roomService.updateRoom(id, room);

        RoomResponseDto responseDto = new RoomResponseDto();
        responseDto.setId(updatedRoom.getId());
        responseDto.setName(updatedRoom.getName());
        responseDto.setMaxGuests(updatedRoom.getMaxGuests());
        responseDto.setHasTechnicalEquipment(updatedRoom.isHasTechnicalEquipment());

        return ResponseEntity.ok(responseDto);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRoom(@PathVariable Integer id) {
        roomService.deleteRoom(id);
        return ResponseEntity.noContent().build();
    }
}
