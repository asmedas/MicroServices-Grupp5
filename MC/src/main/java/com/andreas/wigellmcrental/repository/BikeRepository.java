package com.andreas.wigellmcrental.repository;

import com.andreas.wigellmcrental.entity.Bike;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BikeRepository extends JpaRepository<Bike, Long> {
    List<Bike> findByAvailableTrue();
}

