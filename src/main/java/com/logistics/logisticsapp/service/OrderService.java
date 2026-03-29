package com.logistics.logisticsapp.service;

import com.logistics.logisticsapp.dto.*;
import com.logistics.logisticsapp.entity.*;
import com.logistics.logisticsapp.mapper.OrderMapper;
import com.logistics.logisticsapp.repository.*;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final ClientRepository clientRepository;
    private final RouteRepository routeRepository;
    private final CargoRepository cargoRepository;
    private final RouteVehicleCargoRepository rvcRepository;

    public OrderService(OrderRepository orderRepository,
                        ClientRepository clientRepository,
                        RouteRepository routeRepository,
                        CargoRepository cargoRepository,
                        RouteVehicleCargoRepository rvcRepository) {
        this.orderRepository = orderRepository;
        this.clientRepository = clientRepository;
        this.routeRepository = routeRepository;
        this.cargoRepository = cargoRepository;
        this.rvcRepository = rvcRepository;
    }
    public List<OrderResponseDto> getAllOptimized() {

        List<com.logistics.logisticsapp.entity.Order> orders =
            orderRepository.findAllWithRelations();

        return orders.stream()
            .map(OrderMapper::toDtoWithRelations)
            .toList();
    }
    // 🔥 CREATE ORDER
    public OrderResponseDto create(OrderRequestDto dto) {

        if (dto.getItems() == null || dto.getItems().isEmpty()) {
            throw new RuntimeException("Items must not be empty");
        }

        // 🔹 создаём Order
        Order order = new Order();
        order.setPrice(dto.getPrice());
        order.setStatus(dto.getStatus());

        Client client = clientRepository.findById(dto.getClientId())
            .orElseThrow(() -> new RuntimeException("Client not found"));

        order.setClient(client);

        order = orderRepository.save(order);

        // 🔥 создаём связи через ID
        for (RouteVehicleCargoRequestDto item : dto.getItems()) {

            Route route = routeRepository.findById(item.getRouteId())
                .orElseThrow(() -> new RuntimeException("Route not found"));

            Cargo cargo = cargoRepository.findById(item.getCargoId())
                .orElseThrow(() -> new RuntimeException("Cargo not found"));

            RouteVehicleCargo rvc = new RouteVehicleCargo();
            rvc.setOrder(order);
            rvc.setRoute(route);
            rvc.setCargo(cargo);
            rvc.setVehicle(null); // позже добавится

            rvcRepository.save(rvc);
        }

        return OrderMapper.toDtoWithRelations(order);
    }

    // 🔥 GET ALL
    public List<OrderResponseDto> getAll() {
        return orderRepository.findAll()
            .stream()
            .map(OrderMapper::toDtoWithRelations)
            .toList();
    }

    // 🔥 GET BY ID
    public OrderResponseDto getById(Long id) {
        Order order = orderRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Order not found"));

        return OrderMapper.toDtoWithRelations(order);
    }

    public OrderResponseDto update(Long id, OrderRequestDto dto) {

        Order order = orderRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Order not found"));

        // 🔹 обновляем поля
        order.setPrice(dto.getPrice());
        order.setStatus(dto.getStatus());

        // 🔹 обновляем client (если нужно)
        if (dto.getClientId() != null) {
            Client client = clientRepository.findById(dto.getClientId())
                .orElseThrow(() -> new RuntimeException("Client not found"));
            order.setClient(client);
        }

        order = orderRepository.save(order);

        return OrderMapper.toDtoWithRelations(order);
    }

    // 🔥 DELETE
    public void delete(Long id) {
        orderRepository.deleteById(id);
    }
}