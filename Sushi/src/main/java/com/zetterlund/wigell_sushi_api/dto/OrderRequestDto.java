package com.zetterlund.wigell_sushi_api.dto;

import java.util.List;

public class OrderRequestDto {
    private Integer customerId;
    private List<OrderDetailRequestDto> orderDetails;

    // Getters & setters
    public Integer getCustomerId() {
        return customerId;
    }
    public void setCustomerId(Integer customerId) {
        this.customerId = customerId;
    }

    public List<OrderDetailRequestDto> getOrderDetails() {
        return orderDetails;
    }
    public void setOrderDetails(List<OrderDetailRequestDto> orderDetails) {
        this.orderDetails = orderDetails;
    }
}

