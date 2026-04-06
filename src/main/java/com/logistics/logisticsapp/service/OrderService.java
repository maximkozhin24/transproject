package com.logistics.logisticsapp.service;

import com.logistics.logisticsapp.cache.OrderSearchKey;
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
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class OrderService {
    private static final Logger LOG = LoggerFactory.getLogger(OrderService.class);
    private final Map<OrderSearchKey, List<OrderResponseDto>> cache = new HashMap<>();
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

    @Transactional
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
        invalidateCache();
        return OrderMapper.toDtoWithRelations(order);
    }

    public Page<OrderResponseDto> getAll(Pageable pageable) {
        return orderRepository.findAll(pageable)
            .map(OrderMapper::toDtoWithRelations);
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
        invalidateCache();
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
        invalidateCache();
    }

    public List<OrderResponseDto> getOrdersByCargo(String cargoName) {
        List<Order> orders = orderRepository.findOrdersByCargoName(cargoName);
        return orders.stream()
            .map(OrderMapper::toDtoWithRelations)
            .toList();
    }

    public List<OrderResponseDto> getOrdersByCargoNative(String cargoName) {
        return orderRepository.findOrdersByCargoNameNative(cargoName)
            .stream()
            .map(OrderMapper::toDtoWithRelations)
            .toList();
    }

    public List<OrderResponseDto> getOrdersByCargoCached(String cargoName) {

        OrderSearchKey key = new OrderSearchKey(cargoName);


        if (cache.containsKey(key)) {
            LOG.info("Данные взяты из кэша");
            return cache.get(key);
        }


        List<OrderResponseDto> result = orderRepository
            .findOrdersByCargoName(cargoName)
            .stream()
            .map(OrderMapper::toDtoWithRelations)
            .toList();


        cache.put(key, result);

        return result;
    }

    private void invalidateCache() {
        cache.clear();
        LOG.info("Кэш очищен после изменения данных");
    }
}