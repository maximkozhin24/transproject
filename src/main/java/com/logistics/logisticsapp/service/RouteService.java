package com.logistics.logisticsapp.service;

import com.logistics.logisticsapp.dto.RouteRequestDto;
import com.logistics.logisticsapp.dto.RouteResponseDto;
import com.logistics.logisticsapp.entity.Route;
import com.logistics.logisticsapp.mapper.RouteMapper;
import com.logistics.logisticsapp.repository.RouteRepository;
import com.logistics.logisticsapp.repository.OrderRepository;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RouteService {

    private final RouteRepository routeRepository;
    private final OrderRepository orderRepository;

    public RouteService(RouteRepository routeRepository, OrderRepository orderRepository) {
        this.routeRepository = routeRepository;
        this.orderRepository = orderRepository;
    }

    public List<RouteResponseDto> getAll() {
        return routeRepository.findAll()
            .stream()
            .map(RouteMapper::toDto)
            .collect(Collectors.toList());
    }

    public RouteResponseDto getById(Long id) {
        Route route = routeRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Route not found"));

        return RouteMapper.toDto(route);
    }

    public RouteResponseDto create(RouteRequestDto dto) {
        Route route = RouteMapper.toEntity(dto);

        route.setOrder(orderRepository.findById(dto.getOrderId())
            .orElseThrow(() -> new RuntimeException("Order not found")));

        return RouteMapper.toDto(routeRepository.save(route));
    }

    public RouteResponseDto update(Long id, RouteRequestDto dto) {
        Route route = routeRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Route not found"));

        route.setStartLocation(dto.getStartLocation());
        route.setEndLocation(dto.getEndLocation());
        route.setDistance(dto.getDistance());
        route.setOrder(orderRepository.findById(dto.getOrderId()).orElseThrow());

        return RouteMapper.toDto(routeRepository.save(route));
    }

    public void delete(Long id) {
        routeRepository.deleteById(id);
    }
}