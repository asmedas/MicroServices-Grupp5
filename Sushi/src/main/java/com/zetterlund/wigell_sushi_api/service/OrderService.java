package com.zetterlund.wigell_sushi_api.service;

import com.zetterlund.wigell_sushi_api.dto.OrderDetailRequestDto;
import com.zetterlund.wigell_sushi_api.dto.OrderRequestDto;
import com.zetterlund.wigell_sushi_api.entity.Customer;
import com.zetterlund.wigell_sushi_api.entity.Dish;
import com.zetterlund.wigell_sushi_api.entity.Order;
import com.zetterlund.wigell_sushi_api.entity.OrderDetails;
import com.zetterlund.wigell_sushi_api.repository.CustomerRepository;
import com.zetterlund.wigell_sushi_api.repository.DishRepository;
import com.zetterlund.wigell_sushi_api.repository.OrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderService {
    private static final Logger logger = LoggerFactory.getLogger(OrderService.class);
    private final CustomerRepository customerRepository;
    private final OrderRepository orderRepository;
    private final DishRepository dishRepository;

    public OrderService(OrderRepository orderRepository,  CustomerRepository customerRepository,  DishRepository dishRepository) {
        this.customerRepository = customerRepository;
        this.orderRepository = orderRepository;
        this.dishRepository = dishRepository;
    }

    public List<Order> getOrdersByCustomerId(Integer customerId) {
        logger.info("getOrdersByCustomerId customerId={}", customerId);
        return orderRepository.findAll()
                .stream()
                .filter(order -> order.getCustomer().getId().equals(customerId))
                .toList();
    }

    public Order addOrder(OrderRequestDto dto) {
        logger.info("Adding order.");
        Customer customer = customerRepository.findById(dto.getCustomerId())
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        Order order = new Order();
        order.setCustomer(customer);

        // Summera totalpris
        BigDecimal totalPrice = BigDecimal.ZERO;

        List<OrderDetails> details = new ArrayList<>();
        for (OrderDetailRequestDto detailDto : dto.getOrderDetails()) {
            Dish dish = dishRepository.findById(detailDto.getDishId())
                    .orElseThrow(() -> new RuntimeException("Dish not found"));

            OrderDetails orderDetail = new OrderDetails();
            orderDetail.setOrder(order);
            orderDetail.setDish(dish);
            orderDetail.setCount(detailDto.getQuantity());

            // Summera pris
            totalPrice = totalPrice.add(dish.getPriceInSek().multiply(BigDecimal.valueOf(detailDto.getQuantity())));

            details.add(orderDetail);
        }

        order.setOrderDetails(details);
        order.setTotalPriceInSek(totalPrice);

        return orderRepository.save(order);
    }

    public Order getOrderById(Integer id) {
        logger.info("Getting order {}.", id);
        return orderRepository.findById(id).orElseThrow(() -> new RuntimeException("Order not found"));
    }
}
