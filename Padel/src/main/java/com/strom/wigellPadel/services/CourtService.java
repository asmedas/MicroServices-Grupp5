package com.strom.wigellPadel.services;

import com.strom.wigellPadel.dto.CourtCreateDto;
import com.strom.wigellPadel.dto.CourtDto;
import com.strom.wigellPadel.dto.CourtUpdateDto;
import com.strom.wigellPadel.entities.Court;
import com.strom.wigellPadel.mapper.CourtMapper;
import com.strom.wigellPadel.mapper.CustomerMapper;
import com.strom.wigellPadel.repositories.BookingRepo;
import com.strom.wigellPadel.repositories.CourtRepo;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

//TODO Lägg till loggning som sparas till fil på samtliga metoder!!!

@Service
public class CourtService {

    private final CourtRepo courtRepo;
    private final BookingRepo bookingRepo;

    public CourtService(CourtRepo courtRepo, BookingRepo bookingRepo) {
        this.courtRepo = courtRepo;
        this.bookingRepo = bookingRepo;
    }

    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('ADMIN')")
    public List<CourtDto> getAllCourts() {
        return courtRepo.findAll().stream()
                .map(CourtMapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('ADMIN')")
    public CourtDto getCourt(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Id är null");
        }
        return courtRepo.findById(id)
                .map(CourtMapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException("Padelbana med id " + id + " hittades inte"));
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public CourtDto createCourt(CourtCreateDto dto) {
        if (dto == null) {
            throw new IllegalArgumentException("Body är null");
        }
        if (dto.information() == null || dto.information().isEmpty()
        || dto.price() < 0) {
            throw new IllegalArgumentException("Information är null eller pris lägre än 0");
        }

        Court court = new Court(
                dto.information(),
                dto.price(),
                true
        );

        Court savedCourt = courtRepo.save(court);
        return CourtMapper.toDto(savedCourt);
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public CourtDto updateCourt(Long id, CourtUpdateDto dto) {
        if (dto == null) {
            throw new IllegalArgumentException("Body är null");
        }
        if (dto.information() == null || dto.information().isEmpty()
                || dto.price() < 0) {
            throw new IllegalArgumentException("Information är null eller pris lägre än 0");
        }

        Court court = courtRepo.findById(id)
                        .orElseThrow(() -> new EntityNotFoundException("Padelbana med id " + id + " hittades inte"));
        court.setInformation(dto.information());
        court.setPrice(dto.price());
        court.setAvailable(dto.available());
        courtRepo.save(court);

        return CourtMapper.toDto(court);
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteCourt(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Id är null");
        }
        Court court = courtRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Padelbana med id " + id + " hittades inte"));
        courtRepo.delete(court);
    }

}
