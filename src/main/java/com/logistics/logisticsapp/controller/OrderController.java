package com.logistics.logisticsapp.controller;

import com.logistics.logisticsapp.dto.OrderDto;
import com.logistics.logisticsapp.service.OrderService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    public List<OrderDto> getOrders() {
        return orderService.getAllOrders();
    }

    @PostMapping
    public OrderDto createOrder(@RequestBody OrderDto dto) {
        return orderService.createOrder(dto);
    }
}
