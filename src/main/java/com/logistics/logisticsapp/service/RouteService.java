package com.logistics.logisticsapp.service;

import com.logistics.logisticsapp.dto.RouteRequestDto;
import com.logistics.logisticsapp.dto.RouteResponseDto;
import com.logistics.logisticsapp.entity.Cargo;
import com.logistics.logisticsapp.entity.Order;
import com.logistics.logisticsapp.entity.Route;
import com.logistics.logisticsapp.entity.RouteVehicleCargo;
import com.logistics.logisticsapp.mapper.RouteMapper;
import com.logistics.logisticsapp.repository.CargoRepository;
import com.logistics.logisticsapp.repository.OrderRepository;
import com.logistics.logisticsapp.repository.RouteRepository;

import com.logistics.logisticsapp.repository.RouteVehicleCargoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RouteService {

    private final RouteRepository routeRepository;
    private final OrderRepository orderRepository;
    private final RouteVehicleCargoRepository rvcRepository;
    private final CargoRepository cargoRepository;
    public RouteService(RouteRepository routeRepository, OrderRepository orderRepository,
                        RouteVehicleCargoRepository rvcRepository, CargoRepository cargoRepository) {
        this.routeRepository = routeRepository;
        this.orderRepository = orderRepository;
        this.rvcRepository = rvcRepository;
        this.cargoRepository = cargoRepository;
    }

    public RouteResponseDto create(RouteRequestDto dto) {

        Route route = new Route();
        route.setStartLocation(dto.getStartLocation());
        route.setEndLocation(dto.getEndLocation());
        route.setDistance(dto.getDistance());

        route = routeRepository.save(route);

        return RouteMapper.toDto(route);
    }

    public List<RouteResponseDto> getAll() {
        return routeRepository.findAll()
            .stream()
            .map(RouteMapper::toDto)
            .toList();
    }
    static final String ERROR_ROUTE = "Route not found";
    public RouteResponseDto getById(Long id) {
        Route route = routeRepository.findById(id)
            .orElseThrow(() -> new RuntimeException(ERROR_ROUTE));

        return RouteMapper.toDto(route);
    }

    public RouteResponseDto update(Long id, RouteRequestDto dto) {

        Route route = routeRepository.findById(id)
            .orElseThrow(() -> new RuntimeException(ERROR_ROUTE));

        route.setStartLocation(dto.getStartLocation());
        route.setEndLocation(dto.getEndLocation());
        route.setDistance(dto.getDistance());

        route = routeRepository.save(route);

        return RouteMapper.toDto(route);
    }

    public void delete(Long routeId) {
        Route route = routeRepository.findById(routeId)
            .orElseThrow(() -> new RuntimeException(ERROR_ROUTE));

        // Находим все RouteVehicleCargo с этим route
        List<RouteVehicleCargo> relations = rvcRepository.findAllByRoute_Id(routeId);

        // Для каждого Order, который использует этот Route
        for (RouteVehicleCargo rvc : relations) {
            Order order = rvc.getOrder();

            // Удаляем все связи Order
            List<RouteVehicleCargo> orderRelations = rvcRepository.findAllByOrder_Id(order.getId());
            rvcRepository.deleteAll(orderRelations);

            // Удаляем все Cargo, которые использовались только этим Order
            for (RouteVehicleCargo or : orderRelations) {
                Cargo cargo = or.getCargo();
                boolean usedElsewhere = rvcRepository.existsByCargo_Id(cargo.getId());
                if (!usedElsewhere) {
                    cargoRepository.delete(cargo);
                }
            }

            // Удаляем сам Order
            orderRepository.delete(order);
        }

        // Удаляем Route
        routeRepository.delete(route);
    }
}