package com.zetterlund.wigell_sushi_api.service;

import com.zetterlund.wigell_sushi_api.dto.DishCreationRequestDto;
import com.zetterlund.wigell_sushi_api.entity.Dish;
import com.zetterlund.wigell_sushi_api.exception.ResourceNotFoundException;
import com.zetterlund.wigell_sushi_api.repository.DishRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DishService {
    private final DishRepository dishRepository;
    private final CurrencyConverterService currencyConverterService;

    public DishService(DishRepository dishRepository, CurrencyConverterService currencyConverterService) {
        this.dishRepository = dishRepository;
        this.currencyConverterService = currencyConverterService;
    }

    public List<Dish> getAllDishes() {
        List<Dish> dishes = dishRepository.findAll();
        dishes.forEach(dish -> {
            double convertedPrice = currencyConverterService.convertCurrency("JPY", dish.getPriceInSek());
            dish.setPriceInJpy(convertedPrice);
        });
        return dishes;
    }

    public Dish addDish(Dish dish) {
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
