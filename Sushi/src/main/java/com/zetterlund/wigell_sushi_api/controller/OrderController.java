package com.zetterlund.wigell_sushi_api.controller;

import com.zetterlund.wigell_sushi_api.dto.OrderDto;
import com.zetterlund.wigell_sushi_api.dto.OrderOverviewDto;
import com.zetterlund.wigell_sushi_api.dto.OrderRequestDto;
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
    public ResponseEntity<List<OrderOverviewDto>> getOrdersByCustomerId(@RequestParam Integer customerId) {
        List<Order> orders = orderService.getOrdersByCustomerId(customerId);

        List<OrderOverviewDto> dtoList = orders.stream().map(order -> {
            OrderOverviewDto dto = new OrderOverviewDto();
            dto.setOrderId(order.getId());
            dto.setCustomerName(order.getCustomer().getFirstName());
            dto.setTotalPriceInSek(order.getTotalPriceInSek());
            return dto;
        }).toList();

        return ResponseEntity.ok(dtoList);
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
    public ResponseEntity<OrderOverviewDto> createOrder(@RequestBody OrderRequestDto orderDto) {
        Order createdOrder = orderService.addOrder(orderDto);

        OrderOverviewDto responseDto = new OrderOverviewDto();
        responseDto.setOrderId(createdOrder.getId());
        responseDto.setCustomerName(createdOrder.getCustomer().getFirstName());
        responseDto.setTotalPriceInSek(createdOrder.getTotalPriceInSek());

        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }
}
