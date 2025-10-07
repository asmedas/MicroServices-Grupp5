package com.zetterlund.wigell_sushi_api.service;

import com.zetterlund.wigell_sushi_api.entity.Dish;
import com.zetterlund.wigell_sushi_api.repository.DishRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DishService {
    private final DishRepository dishRepository;

    public DishService(DishRepository dishRepository) {
        this.dishRepository = dishRepository;
    }

    public List<Dish> getAllDishes() {
        return dishRepository.findAll();
    }

    public Dish addDish(Dish dish) {
        return dishRepository.save(dish);
    }
}
