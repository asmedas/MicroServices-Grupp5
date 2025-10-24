package com.zetterlund.wigell_sushi_api.service;

import com.zetterlund.wigell_sushi_api.entity.Order;
import com.zetterlund.wigell_sushi_api.repository.OrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderService {
    private static final Logger logger = LoggerFactory.getLogger(OrderService.class);
    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public List<Order> getOrdersByCustomerId(Long customerId) {
        logger.info("getOrdersByCustomerId customerId={}", customerId);
        return orderRepository.findAll()
                .stream()
                .filter(order -> order.getCustomer().getId().equals(customerId))
                .toList();
    }

    public Order addOrder(Order order) {
        logger.info("Adding order {}.", order);
        return orderRepository.save(order);
    }

    public Order getOrderById(Integer id) {
        logger.info("Getting order {}.", id);
        return orderRepository.findById(id).orElseThrow(() -> new RuntimeException("Order not found"));
    }
}
