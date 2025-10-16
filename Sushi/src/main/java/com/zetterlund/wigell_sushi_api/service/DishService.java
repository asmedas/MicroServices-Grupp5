package com.zetterlund.wigell_sushi_api.service;

import com.zetterlund.wigell_sushi_api.entity.Dish;
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
}
