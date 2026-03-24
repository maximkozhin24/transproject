package com.logistics.logisticsapp.service;

import com.logistics.logisticsapp.dto.RouteRequestDto;
import com.logistics.logisticsapp.dto.RouteResponseDto;
import com.logistics.logisticsapp.entity.Route;
import com.logistics.logisticsapp.mapper.RouteMapper;
import com.logistics.logisticsapp.repository.RouteRepository;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RouteService {

    private final RouteRepository routeRepository;

    public RouteService(RouteRepository routeRepository) {
        this.routeRepository = routeRepository;
    }

    // 🔥 CREATE
    public RouteResponseDto create(RouteRequestDto dto) {

        Route route = new Route();
        route.setStartLocation(dto.getStartLocation());
        route.setEndLocation(dto.getEndLocation());
        route.setDistance(dto.getDistance());

        route = routeRepository.save(route);

        return RouteMapper.toDto(route);
    }

    // 🔥 GET ALL
    public List<RouteResponseDto> getAll() {
        return routeRepository.findAll()
            .stream()
            .map(RouteMapper::toDto)
            .collect(Collectors.toList());
    }

    // 🔥 GET BY ID
    public RouteResponseDto getById(Long id) {
        Route route = routeRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Route not found"));

        return RouteMapper.toDto(route);
    }

    // 🔥 UPDATE
    public RouteResponseDto update(Long id, RouteRequestDto dto) {

        Route route = routeRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Route not found"));

        route.setStartLocation(dto.getStartLocation());
        route.setEndLocation(dto.getEndLocation());
        route.setDistance(dto.getDistance());

        route = routeRepository.save(route);

        return RouteMapper.toDto(route);
    }

    // 🔥 DELETE
    public void delete(Long id) {
        routeRepository.deleteById(id);
    }
}