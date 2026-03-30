package com.logistics.logisticsapp.service;

import com.logistics.logisticsapp.dto.OrderRequestDto;
import com.logistics.logisticsapp.dto.OrderResponseDto;
import com.logistics.logisticsapp.dto.RouteVehicleCargoRequestDto;
import com.logistics.logisticsapp.entity.Cargo;
import com.logistics.logisticsapp.entity.Client;
import com.logistics.logisticsapp.entity.Order;
import com.logistics.logisticsapp.entity.Route;
import com.logistics.logisticsapp.entity.RouteVehicleCargo;
import com.logistics.logisticsapp.mapper.OrderMapper;
import com.logistics.logisticsapp.repository.CargoRepository;
import com.logistics.logisticsapp.repository.ClientRepository;
import com.logistics.logisticsapp.repository.OrderRepository;
import com.logistics.logisticsapp.repository.RouteRepository;
import com.logistics.logisticsapp.repository.RouteVehicleCargoRepository;
import org.springframework.stereotype.Service;
import java.util.List;

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

    public OrderResponseDto create(OrderRequestDto dto) {

        if (dto.getItems() == null || dto.getItems().isEmpty()) {
            throw new IllegalStateException("Items must not be empty");
        }


        Order order = new Order();
        order.setPrice(dto.getPrice());
        order.setStatus(dto.getStatus());

        Client client = clientRepository.findById(dto.getClientId())
            .orElseThrow(() -> new RuntimeException("Client not found"));

        order.setClient(client);

        order = orderRepository.save(order);

        for (RouteVehicleCargoRequestDto item : dto.getItems()) {

            Route route = routeRepository.findById(item.getRouteId())
                .orElseThrow(() -> new RuntimeException("Route not found"));

            Cargo cargo = cargoRepository.findById(item.getCargoId())
                .orElseThrow(() -> new RuntimeException("Cargo not found"));

            RouteVehicleCargo rvc = new RouteVehicleCargo();
            rvc.setOrder(order);
            rvc.setRoute(route);
            rvc.setCargo(cargo);
            rvc.setVehicle(null);

            rvcRepository.save(rvc);
        }

        return OrderMapper.toDtoWithRelations(order);
    }

    public List<OrderResponseDto> getAll() {
        return orderRepository.findAll()
            .stream()
            .map(OrderMapper::toDtoWithRelations)
            .toList();
    }
    static final String ERROR_ORDER = "Order not found";
    public OrderResponseDto getById(Long id) {
        Order order = orderRepository.findById(id)
            .orElseThrow(() -> new RuntimeException(ERROR_ORDER));

        return OrderMapper.toDtoWithRelations(order);
    }

    public OrderResponseDto update(Long id, OrderRequestDto dto) {

        Order order = orderRepository.findById(id)
            .orElseThrow(() -> new RuntimeException(ERROR_ORDER));

        order.setPrice(dto.getPrice());
        order.setStatus(dto.getStatus());

        if (dto.getClientId() != null) {
            Client client = clientRepository.findById(dto.getClientId())
                .orElseThrow(() -> new RuntimeException(ERROR_ORDER));
            order.setClient(client);
        }

        order = orderRepository.save(order);

        return OrderMapper.toDtoWithRelations(order);
    }

    public void delete(Long orderId) {

        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new RuntimeException(ERROR_ORDER));

        List<RouteVehicleCargo> relations = rvcRepository.findAllByOrderId(orderId);

        rvcRepository.deleteAll(relations);

        for (RouteVehicleCargo rvc : relations) {
            Cargo cargo = rvc.getCargo();

            boolean usedElsewhere = rvcRepository.existsByCargoId(cargo.getId());
            if (!usedElsewhere) {
                cargoRepository.delete(cargo);
            }
        }

        orderRepository.delete(order);
    }
}