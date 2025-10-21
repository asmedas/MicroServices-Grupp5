package com.zetterlund.wigell_sushi_api.service;

import com.zetterlund.wigell_sushi_api.entity.Room;
import com.zetterlund.wigell_sushi_api.repository.RoomRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoomService {
    private final RoomRepository roomRepository;

    public RoomService(RoomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }

    public List<Room> getAllRooms() {
        return roomRepository.findAll();
    }

    public Room addRoom(Room room) {
        return roomRepository.save(room);
    }

    public Room getRoomById(Integer id) {
        return roomRepository.findById(id).orElseThrow(() -> new RuntimeException("Room not found"));
    }

    public Room updateRoom(Integer id, Room updatedRoom) {
        Room existingRoom = getRoomById(id);
        existingRoom.setName(updatedRoom.getName());
        existingRoom.setMaxGuests(updatedRoom.getMaxGuests());
        existingRoom.setHasTechnicalEquipment(updatedRoom.isHasTechnicalEquipment());
        return roomRepository.save(existingRoom);
    }

    public void deleteRoom(Integer id) {
        roomRepository.deleteById(id);
    }
}
