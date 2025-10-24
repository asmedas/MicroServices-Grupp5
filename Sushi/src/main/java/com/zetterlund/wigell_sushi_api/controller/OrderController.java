package com.zetterlund.wigell_sushi_api.controller;

import com.zetterlund.wigell_sushi_api.dto.OrderDto;
import com.zetterlund.wigell_sushi_api.dto.OrderOverviewDto;
import com.zetterlund.wigell_sushi_api.entity.Order;
import com.zetterlund.wigell_sushi_api.service.OrderService;
import com.zetterlund.wigell_sushi_api.repository.OrderRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {
    private final OrderService orderService;
    private final OrderRepository orderRepository;

    public OrderController(OrderService orderService, OrderRepository orderRepository) {
        this.orderService = orderService;
        this.orderRepository = orderRepository;
    }

    // används enbart i felsökningssyfte (hitta orderID)
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/all")
    public ResponseEntity<List<OrderDto>> getAllOrders() {
        List<OrderDto> ordersDto = orderRepository.findAll()
                .stream()
                .map(order -> {
                    OrderDto dto = new OrderDto();
                    dto.setOrderId(order.getId());
                    dto.setCustomerName(order.getCustomer().getFirstName());
                    dto.setTotalPriceInSek(order.getTotalPriceInSek());
                    return dto;
                })
                .toList();

        return ResponseEntity.ok(ordersDto);
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping
    public ResponseEntity<List<Order>> getOrdersByCustomerId(@RequestParam Long customerId) {
        List<Order> orders = orderService.getOrdersByCustomerId(customerId);
        return ResponseEntity.ok(orders);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{orderId}")
    public ResponseEntity<OrderOverviewDto> getOrderById(@PathVariable Integer orderId) {
        Order order = orderService.getOrderById(orderId);

        OrderOverviewDto orderDto = new OrderOverviewDto();
        orderDto.setOrderId(order.getId());
        orderDto.setCustomerName(order.getCustomer().getFirstName());
        orderDto.setTotalPriceInSek(order.getTotalPriceInSek());

        return ResponseEntity.ok(orderDto);
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping
    public ResponseEntity<Order> createOrder(@RequestBody Order order) {
        Order createdOrder = orderService.addOrder(order);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdOrder);
    }
}
