package com.logistics.logisticsapp.mapper;

import com.logistics.logisticsapp.dto.RouteDto;
import com.logistics.logisticsapp.entity.Route;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class RouteMapper {

    public RouteDto toDto(Route route) {

        RouteDto dto = new RouteDto();

        dto.setId(route.getId());
        dto.setStartLocation(route.getStartLocation());
        dto.setEndLocation(route.getEndLocation());
        dto.setDistance(route.getDistance());

        if (route.getOrder() != null) {
            dto.setOrderId(route.getOrder().getId());
        }

        if (route.getVehicles() != null) {
            List<Long> vehicleIds = route.getVehicles()
                .stream()
                .map(vehicle -> vehicle.getId())
                .collect(Collectors.toList());

            dto.setVehicleIds(vehicleIds);
        }

        if (route.getCargos() != null) {
            List<Long> cargoIds = route.getCargos()
                .stream()
                .map(cargo -> cargo.getId())
                .collect(Collectors.toList());

            dto.setCargoIds(cargoIds);
        }

        return dto;
    }
}