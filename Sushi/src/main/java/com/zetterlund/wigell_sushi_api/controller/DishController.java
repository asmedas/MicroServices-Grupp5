package com.zetterlund.wigell_sushi_api.controller;

import com.zetterlund.wigell_sushi_api.entity.Dish;
import com.zetterlund.wigell_sushi_api.service.DishService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/dishes")
public class DishController {
    private final DishService dishService;

    public DishController(DishService dishService) {
        this.dishService = dishService;
    }

    @GetMapping
    public ResponseEntity<List<Dish>> getAllDishes() {
        return ResponseEntity.ok(dishService.getAllDishes());
    }

    @PostMapping
    public ResponseEntity<Dish> createDish(@RequestBody Dish dish) {
        Dish createdDish = dishService.addDish(dish);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdDish);
    }
}
