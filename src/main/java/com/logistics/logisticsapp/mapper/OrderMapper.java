package com.logistics.logisticsapp.mapper;

import com.logistics.logisticsapp.entity.Order;
import org.springframework.stereotype.Component;
import com.logistics.logisticsapp.entity.Client;
import com.logistics.logisticsapp.dto.OrderDto;

@Component
public class OrderMapper {

    public OrderDto toDTO(Order order) {

        OrderDto dto = new OrderDto();

        dto.setId(order.getId());
        dto.setStatus(order.getStatus());
        dto.setPrice(order.getPrice());
        dto.setClientId(order.getClient().getId());

        return dto;
    }

    public Order toEntity(OrderDto dto, Client client) {

        Order order = new Order();

        order.setId(dto.getId());
        order.setStatus(dto.getStatus());
        order.setPrice(dto.getPrice());
        order.setClient(client);

        return order;
    }
}
