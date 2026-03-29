package com.logistics.logisticsapp.mapper;

import com.logistics.logisticsapp.dto.RouteRequestDto;
import com.logistics.logisticsapp.dto.RouteResponseDto;
import com.logistics.logisticsapp.entity.Route;

public class RouteMapper {
private RouteMapper(){}
    public static Route toEntity(RouteRequestDto dto) {
        Route route = new Route();
        route.setStartLocation(dto.getStartLocation());
        route.setEndLocation(dto.getEndLocation());
        route.setDistance(dto.getDistance());
        return route;
    }

    public static RouteResponseDto toDto(Route route) {
        RouteResponseDto dto = new RouteResponseDto();
        dto.setId(route.getId());
        dto.setStartLocation(route.getStartLocation());
        dto.setEndLocation(route.getEndLocation());
        dto.setDistance(route.getDistance());
        return dto;
    }
}