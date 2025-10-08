package com.strom.wigellPadel.repositories;

import com.strom.wigellPadel.entities.Court;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourtRepo extends JpaRepository<Court, Integer> {
}
