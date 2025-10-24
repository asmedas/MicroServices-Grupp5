package com.zetterlund.wigell_sushi_api.service;

import com.zetterlund.wigell_sushi_api.dto.DishCreationRequestDto;
import com.zetterlund.wigell_sushi_api.entity.Dish;
import com.zetterlund.wigell_sushi_api.exception.ResourceNotFoundException;
import com.zetterlund.wigell_sushi_api.repository.DishRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class DishService {
    private static final Logger logger = LoggerFactory.getLogger(DishService.class);
    private final DishRepository dishRepository;
    private final CallCurrencyApiService callCurrencyApiService;

    public DishService(DishRepository dishRepository, CallCurrencyApiService callCurrencyApiService) {
        this.dishRepository = dishRepository;
        this.callCurrencyApiService = callCurrencyApiService;
    }

    public List<Dish> getAllDishes() {
        logger.info("getAllDishes service class");

        List<Dish> dishes = dishRepository.findAll();
        dishes.forEach(dish -> {
            BigDecimal convertedPrice = callCurrencyApiService.convertFromSEKToJPY(dish.getPriceInSek());
            dish.setPriceInJpy(convertedPrice);
        });
        return dishes;
    }

    public Dish addDish(Dish dish) {
        logger.info("addDish service class");
        return dishRepository.save(dish);
    }

    public Dish updateDish(Integer dishId, DishCreationRequestDto dishDto) {
        Dish existingDish = dishRepository.findById(dishId)
                .orElseThrow(() -> new ResourceNotFoundException("Dish with id " + dishId + " not found."));

        existingDish.setName(dishDto.getName());
        existingDish.setPriceInSek(dishDto.getPriceInSek());
        existingDish.setDescription(dishDto.getDescription());

        return dishRepository.save(existingDish);
    }

    public void deleteDishById(Integer dishId) {
        if (!dishRepository.existsById(dishId)) {
            throw new ResourceNotFoundException("Dish with id " + dishId + " not found.");
        }
        dishRepository.deleteById(dishId);
    }
}
