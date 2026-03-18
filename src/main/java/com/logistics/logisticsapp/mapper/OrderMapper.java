package com.logistics.logisticsapp.mapper;

import com.logistics.logisticsapp.dto.OrderRequestDto;
import com.logistics.logisticsapp.dto.OrderResponseDto;
import com.logistics.logisticsapp.entity.Order;

public class OrderMapper {

    public static Order toEntity(OrderRequestDto dto) {
        Order order = new Order();

        order.setPrice(dto.getPrice());
        order.setStatus(dto.getStatus());

        return order;
    }

    public static OrderResponseDto toDto(Order order) {
        OrderResponseDto dto = new OrderResponseDto();

        dto.setId(order.getId());
        dto.setPrice(order.getPrice());
        dto.setStatus(order.getStatus());

        if (order.getClient() != null) {
            dto.setClientId(order.getClient().getId());
        }

        return dto;
    }
}