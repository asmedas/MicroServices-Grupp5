package com.strom.wigellPadel.services;

import com.strom.wigellPadel.dto.CourtCreateDto;
import com.strom.wigellPadel.dto.CourtDto;
import com.strom.wigellPadel.dto.CourtUpdateDto;
import com.strom.wigellPadel.entities.Court;
import com.strom.wigellPadel.mapper.CourtMapper;
import com.strom.wigellPadel.repositories.BookingRepo;
import com.strom.wigellPadel.repositories.CourtRepo;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CourtService {

    private static final Logger logger = LoggerFactory.getLogger(CourtService.class);
    private final CourtRepo courtRepo;
    private final BookingRepo bookingRepo;
    private final CurrencyConverterClient converterClient;

    public CourtService(CourtRepo courtRepo, BookingRepo bookingRepo, CurrencyConverterClient converterClient) {
        this.courtRepo = courtRepo;
        this.bookingRepo = bookingRepo;
        this.converterClient = converterClient;
        logger.debug("CourtService initialized");
    }

    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('ADMIN')")
    public List<CourtDto> getAllCourts() {
        logger.info("Hämtar alla padelbanor");
        try {
            List<CourtDto> courts = courtRepo.findAll().stream()
                    .map(this::toDtoWithEURPrice)
                    .toList();
            logger.debug("Lyckades hämta {} padelbanor", courts.size());
            return courts;
        } catch (Exception e) {
            logger.error("Error vid hämtning av padelbanor", e);
            throw e;
        }
    }

    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('ADMIN')")
    public CourtDto getCourt(Long id) {
        logger.info("Hämtar padelbana med id: {}", id);
        try {
            if (id == null) {
                logger.error("Id är null");
                throw new IllegalArgumentException("Id är null");
            }
            Court court = courtRepo.findById(id)
                    .orElseThrow(() -> {
                        logger.error("Padelbana med id {} hittades inte", id);
                        return new EntityNotFoundException("Padelbana med id " + id + " hittades inte");
                    });
            return toDtoWithEURPrice(court);
        } catch (Exception e) {
            logger.error("Error vid hämtning av padelbana med id: {}", id, e);
            throw e;
        }
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public CourtDto createCourt(CourtCreateDto dto) {
        logger.info("Skapar ny padelbana med information: {}", dto.information());
        try {
            if (dto == null) {
                logger.error("Body är null");
                throw new IllegalArgumentException("Body är null");
            }
            if (dto.information() == null || dto.information().isEmpty() || dto.price() < 0) {
                logger.error("Error: Information är null eller pris lägre än 0");
                throw new IllegalArgumentException("Information är null eller pris lägre än 0");
            }

            Court court = new Court(dto.information(), dto.price());
            Court savedCourt = courtRepo.save(court);
            logger.info("Lyckades skapa padelbana med id: {}", savedCourt.getId());
            return toDtoWithEURPrice(savedCourt);
        } catch (Exception e) {
            logger.error("Error vid skapande av padelbana", e);
            throw e;
        }
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public CourtDto updateCourt(Long id, CourtUpdateDto dto) {
        logger.info("Uppdaterar padelbana med id: {}", id);
        try {
            if (dto == null) {
                logger.error("Body är null");
                throw new IllegalArgumentException("Body är null");
            }
            if (dto.information() == null || dto.information().isEmpty() || dto.price() < 0) {
                logger.error("Error: Information är null eller pris lägre än 0");
                throw new IllegalArgumentException("Information är null eller pris lägre än 0");
            }

            Court court = courtRepo.findById(id)
                    .orElseThrow(() -> {
                        logger.error("Padelbana med id {} hittades inte", id);
                        return new EntityNotFoundException("Padelbana med id " + id + " hittades inte");
                    });
            court.setInformation(dto.information());
            court.setPrice(dto.price());
            Court savedCourt = courtRepo.save(court);
            logger.info("Lyckades uppdatera padelbana med id: {}", id);
            return toDtoWithEURPrice(savedCourt);
        } catch (Exception e) {
            logger.error("Error vid uppdatering av padelbana med id: {}", id, e);
            throw e;
        }
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteCourt(Long id) {
        logger.info("Tar bort padelbana med id: {}", id);
        try {
            if (id == null) {
                logger.error("Id är null");
                throw new IllegalArgumentException("Id är null");
            }
            Court court = courtRepo.findById(id)
                    .orElseThrow(() -> {
                        logger.error("Padelbana med id {} hittades inte", id);
                        return new EntityNotFoundException("Padelbana med id " + id + " hittades inte");
                    });
            courtRepo.delete(court);
            logger.info("Lyckades ta bort padelbana med id: {}", id);
        } catch (Exception e) {
            logger.error("Error vid borttag av padelbana med id: {}", id, e);
            throw e;
        }
    }

    private CourtDto toDtoWithEURPrice(Court court) {
        double priceInEUR;
        try {
            priceInEUR = converterClient.convertToEUR(court.getPrice());
        } catch (Exception e) {
            logger.warn("Misslyckades att konvertera pris till EUR för padelbana med id: {}. Sätter pris till 0.0", court.getId(), e);
            priceInEUR = 0.0;
        }
        return CourtMapper.toDto(court, priceInEUR);
    }
}