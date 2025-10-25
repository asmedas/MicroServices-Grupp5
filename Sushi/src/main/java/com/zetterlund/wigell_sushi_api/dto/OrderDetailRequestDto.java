package com.zetterlund.wigell_sushi_api.dto;

public class OrderDetailRequestDto {
    private Integer dishId;
    private int quantity;

    // Getters & setters
    public Integer getDishId() {
        return dishId;
    }

    public void setDishId(Integer dishId) {
        this.dishId = dishId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
