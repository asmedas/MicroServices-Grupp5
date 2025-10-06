package com.zetterlund.wigell_sushi_api.repository;

import com.zetterlund.wigell_sushi_api.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {
}
