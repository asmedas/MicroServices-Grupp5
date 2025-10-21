package com.zetterlund.wigell_sushi_api.controller;

import com.zetterlund.wigell_sushi_api.dto.OrderOverviewDto;
import com.zetterlund.wigell_sushi_api.entity.Order;
import com.zetterlund.wigell_sushi_api.service.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<Order>> getOrdersByCustomerId(@RequestParam Long customerId) {
        List<Order> orders = orderService.getOrdersByCustomerId(customerId);
        return ResponseEntity.ok(orders);
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @GetMapping("/{orderId}")
    public ResponseEntity<OrderOverviewDto> getOrderById(@PathVariable Integer orderId) {
        Order order = orderService.getOrderById(orderId);

        OrderOverviewDto dto = new OrderOverviewDto();
        dto.setOrderId(order.getId());
        dto.setCustomerName(order.getCustomer().getFirstName());
        dto.setTotalPriceInSek(order.getTotalPriceInSek());

        return ResponseEntity.ok(dto);
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<Order> createOrder(@RequestBody Order order) {
        Order createdOrder = orderService.addOrder(order);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdOrder);
    }
}
