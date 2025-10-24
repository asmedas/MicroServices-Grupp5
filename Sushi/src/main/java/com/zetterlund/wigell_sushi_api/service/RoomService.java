package com.zetterlund.wigell_sushi_api.service;

import com.zetterlund.wigell_sushi_api.entity.Room;
import com.zetterlund.wigell_sushi_api.repository.RoomRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoomService {
    private static final Logger logger = LoggerFactory.getLogger(RoomService.class);
    private final RoomRepository roomRepository;

    public RoomService(RoomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }

    public List<Room> getAllRooms() {
        logger.info("getAllRooms service class");
        return roomRepository.findAll();
    }

    public Room addRoom(Room room) {
        logger.info("Room with id {} has been added", room.getId());
        return roomRepository.save(room);
    }

    public Room getRoomById(Integer id) {
        logger.info("Room fetched");
        return roomRepository.findById(id).orElseThrow(() -> new RuntimeException("Room not found"));
    }

    public Room updateRoom(Integer id, Room updatedRoom) {
        logger.info("Room with id {} has been updated", id);
        Room existingRoom = getRoomById(id);
        existingRoom.setName(updatedRoom.getName());
        existingRoom.setMaxGuests(updatedRoom.getMaxGuests());
        existingRoom.setHasTechnicalEquipment(updatedRoom.isHasTechnicalEquipment());
        return roomRepository.save(existingRoom);
    }

    public void deleteRoom(Integer id) {
        logger.info("Room with id {} has been deleted", id);
        roomRepository.deleteById(id);
    }
}
