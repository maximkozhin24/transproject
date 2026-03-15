package com.logistics.logisticsapp.service;

import com.logistics.logisticsapp.dto.RouteDto;
import com.logistics.logisticsapp.entity.Route;
import com.logistics.logisticsapp.entity.Order;
import com.logistics.logisticsapp.entity.Vehicle;
import com.logistics.logisticsapp.entity.Cargo;
import com.logistics.logisticsapp.mapper.RouteMapper;
import com.logistics.logisticsapp.repository.RouteRepository;
import com.logistics.logisticsapp.repository.OrderRepository;
import com.logistics.logisticsapp.repository.VehicleRepository;
import com.logistics.logisticsapp.repository.CargoRepository;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RouteService {

    private final RouteRepository routeRepository;
    private final OrderRepository orderRepository;
    private final VehicleRepository vehicleRepository;
    private final CargoRepository cargoRepository;
    private final RouteMapper routeMapper;

    public RouteService(RouteRepository routeRepository,
                        OrderRepository orderRepository,
                        VehicleRepository vehicleRepository,
                        CargoRepository cargoRepository,
                        RouteMapper routeMapper) {

        this.routeRepository = routeRepository;
        this.orderRepository = orderRepository;
        this.vehicleRepository = vehicleRepository;
        this.cargoRepository = cargoRepository;
        this.routeMapper = routeMapper;
    }

    // Получить все маршруты
    public List<RouteDto> getAllRoutes() {
        return routeRepository.findAll()
            .stream()
            .map(routeMapper::toDto)
            .toList();
    }

    // Получить маршрут по ID
    public RouteDto getRouteById(Long id) {

        Route route = routeRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Route not found"));

        return routeMapper.toDto(route);
    }

    // Создать маршрут
    public RouteDto createRoute(RouteDto dto) {

        Order order = orderRepository.findById(dto.getOrderId())
            .orElseThrow(() -> new RuntimeException("Order not found"));

        Route route = new Route();

        route.setStartLocation(dto.getStartLocation());
        route.setEndLocation(dto.getEndLocation());
        route.setDistance(dto.getDistance());
        route.setOrder(order);

        Route savedRoute = routeRepository.save(route);

        return routeMapper.toDto(savedRoute);
    }

    // Добавить транспорт к маршруту (ManyToMany)
    public RouteDto addVehicleToRoute(Long routeId, Long vehicleId) {

        Route route = routeRepository.findById(routeId)
            .orElseThrow(() -> new RuntimeException("Route not found"));

        Vehicle vehicle = vehicleRepository.findById(vehicleId)
            .orElseThrow(() -> new RuntimeException("Vehicle not found"));

        route.getVehicles().add(vehicle);

        routeRepository.save(route);

        return routeMapper.toDto(route);
    }

    // Добавить груз к маршруту (ManyToMany)
    public RouteDto addCargoToRoute(Long routeId, Long cargoId) {

        Route route = routeRepository.findById(routeId)
            .orElseThrow(() -> new RuntimeException("Route not found"));

        Cargo cargo = cargoRepository.findById(cargoId)
            .orElseThrow(() -> new RuntimeException("Cargo not found"));

        route.getCargos().add(cargo);

        routeRepository.save(route);

        return routeMapper.toDto(route);
    }
}
