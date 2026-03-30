package com.logistics.logisticsapp.mapper;

import com.logistics.logisticsapp.dto.ClientRequestDto;
import com.logistics.logisticsapp.dto.ClientResponseDto;
import com.logistics.logisticsapp.dto.OrderResponseDto;
import com.logistics.logisticsapp.entity.Client;
import java.util.List;

public class ClientMapper {
    private ClientMapper() {
    }
    public static Client toEntity(ClientRequestDto dto) {
        Client client = new Client();

        client.setName(dto.getName());
        client.setEmail(dto.getEmail());
        client.setPhone(dto.getPhone());

        return client;
    }

    public static ClientResponseDto toDto(Client client) {
        ClientResponseDto dto = new ClientResponseDto();

        dto.setId(client.getId());
        dto.setName(client.getName());
        dto.setEmail(client.getEmail());
        dto.setPhone(client.getPhone());

        if (client.getOrders() != null) {
            List<OrderResponseDto> orders = client.getOrders()
                .stream()
                .map(OrderMapper::toDtoWithRelations)
                .toList();

            dto.setOrders(orders);
        }

        return dto;
    }
}