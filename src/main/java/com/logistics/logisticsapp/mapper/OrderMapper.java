package com.logistics.logisticsapp.mapper;

import com.logistics.logisticsapp.dto.OrderRequestDto;
import com.logistics.logisticsapp.dto.OrderResponseDto;
import com.logistics.logisticsapp.entity.Order;
import com.logistics.logisticsapp.entity.OrderStatus;

public class OrderMapper {

    // 🔥 DTO → Entity
    public static Order toEntity(OrderRequestDto dto) {
        Order order = new Order();

        order.setPrice(dto.getPrice());

        // ✅ конвертация String → Enum
        order.setStatus(dto.getStatus());

        return order;
    }

    // 🔥 Entity → DTO
    public static OrderResponseDto toDto(Order order) {
        OrderResponseDto dto = new OrderResponseDto();

        dto.setId(order.getId());
        dto.setPrice(order.getPrice());

        // ✅ Enum → String
        order.setStatus(dto.getStatus());

        return dto;
    }
}