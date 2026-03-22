package com.logistics.logisticsapp.mapper;

import com.logistics.logisticsapp.dto.RouteVehicleCargoResponseDto;
import com.logistics.logisticsapp.entity.RouteVehicleCargo;

public class RouteVehicleCargoMapper {

    public static RouteVehicleCargoResponseDto toDto(RouteVehicleCargo rvc) {
        RouteVehicleCargoResponseDto dto = new RouteVehicleCargoResponseDto();

        dto.setId(rvc.getId());

        if (rvc.getRoute() != null) {
            dto.setRouteId(rvc.getRoute().getId());
        }

        if (rvc.getVehicle() != null) {
            dto.setVehicleId(rvc.getVehicle().getId());
        }

        if (rvc.getCargo() != null) {
            dto.setCargoId(rvc.getCargo().getId());
        }

        return dto;
    }
}