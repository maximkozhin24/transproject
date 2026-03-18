package com.logistics.logisticsapp.service;

import com.logistics.logisticsapp.dto.OrderRequestDto;
import com.logistics.logisticsapp.dto.OrderResponseDto;
import com.logistics.logisticsapp.entity.Order;
import com.logistics.logisticsapp.entity.Client;
import com.logistics.logisticsapp.mapper.OrderMapper;
import com.logistics.logisticsapp.repository.OrderRepository;
import com.logistics.logisticsapp.repository.ClientRepository;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final ClientRepository clientRepository;

    public OrderService(OrderRepository orderRepository, ClientRepository clientRepository) {
        this.orderRepository = orderRepository;
        this.clientRepository = clientRepository;
    }

    public List<OrderResponseDto> getAll() {
        return orderRepository.findAll()
            .stream()
            .map(OrderMapper::toDto)
            .collect(Collectors.toList());
    }

    public OrderResponseDto getById(Long id) {
        Order order = orderRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Order not found"));

        return OrderMapper.toDto(order);
    }

    public OrderResponseDto create(OrderRequestDto dto) {
        Order order = OrderMapper.toEntity(dto);

        Client client = clientRepository.findById(dto.getClientId())
            .orElseThrow(() -> new RuntimeException("Client not found"));

        order.setClient(client);

        return OrderMapper.toDto(orderRepository.save(order));
    }

    public OrderResponseDto update(Long id, OrderRequestDto dto) {
        Order order = orderRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Order not found"));

        order.setPrice(dto.getPrice());
        order.setStatus(dto.getStatus());

        Client client = clientRepository.findById(dto.getClientId())
            .orElseThrow(() -> new RuntimeException("Client not found"));

        order.setClient(client);

        return OrderMapper.toDto(orderRepository.save(order));
    }

    public void delete(Long id) {
        orderRepository.deleteById(id);
    }
}