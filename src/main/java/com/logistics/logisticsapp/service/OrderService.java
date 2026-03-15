package com.logistics.logisticsapp.service;

import com.logistics.logisticsapp.dto.OrderDto;
import com.logistics.logisticsapp.entity.Client;
import com.logistics.logisticsapp.entity.Order;
import com.logistics.logisticsapp.repository.OrderRepository;
import com.logistics.logisticsapp.repository.ClientRepository;
import com.logistics.logisticsapp.mapper.OrderMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final ClientRepository clientRepository;
    private final OrderMapper orderMapper;

    public OrderService(OrderRepository orderRepository,
                        ClientRepository clientRepository,
                        OrderMapper orderMapper) {

        this.orderRepository = orderRepository;
        this.clientRepository = clientRepository;
        this.orderMapper = orderMapper;
    }

    public OrderDto createOrder(OrderDto dto) {

        Client client = clientRepository.findById(dto.getClientId())
            .orElseThrow();

        Order order = orderMapper.toEntity(dto, client);

        order = orderRepository.save(order);

        return orderMapper.toDTO(order);
    }

    public List<OrderDto> getAllOrders() {

        return orderRepository.findAll()
            .stream()
            .map(orderMapper::toDTO)
            .toList();
    }
}
