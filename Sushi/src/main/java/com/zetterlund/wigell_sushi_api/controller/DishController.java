package com.zetterlund.wigell_sushi_api.controller;

import com.zetterlund.wigell_sushi_api.dto.DishCreationRequestDto;
import com.zetterlund.wigell_sushi_api.entity.Dish;
import com.zetterlund.wigell_sushi_api.exception.BadRequestException;
import com.zetterlund.wigell_sushi_api.exception.ResourceNotFoundException;
import com.zetterlund.wigell_sushi_api.service.DishService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/dishes")
public class DishController {
    private final DishService dishService;

    public DishController(DishService dishService) {
        this.dishService = dishService;
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<Dish>> getAllDishes() {
        return ResponseEntity.ok(dishService.getAllDishes());
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<Dish> createDish(@RequestBody DishCreationRequestDto dishDto) {
        if (dishDto.getName() == null || dishDto.getName().isEmpty()) {
            throw new BadRequestException("Dish name is mandatory.");
        }
        
        Dish dish = new Dish();
        dish.setName(dishDto.getName());
        dish.setPriceInSek(dishDto.getPriceInSek());
        dish.setDescription(dishDto.getDescription());

        Dish createdDish = dishService.addDish(dish);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdDish);
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<Dish> getDishById(@PathVariable Integer id) {
        Dish dish = dishService.getAllDishes()
                .stream()
                .filter(d -> d.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Dish with id " + id + " not found."));
        return ResponseEntity.ok(dish);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{dishId}")
    public ResponseEntity<Dish> updateDish(
            @PathVariable Integer dishId,
            @RequestBody DishCreationRequestDto dishDto) {

        Dish updatedDish = dishService.updateDish(dishId, dishDto);
        return ResponseEntity.ok(updatedDish);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{dishId}")
    public ResponseEntity<Void> deleteDish(@PathVariable Integer dishId) {
        dishService.deleteDishById(dishId);
        return ResponseEntity.noContent().build();
    }
}
