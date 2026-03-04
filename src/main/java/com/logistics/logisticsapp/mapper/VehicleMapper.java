package com.logistics.logisticsapp.mapper;

import com.logistics.logisticsapp.dto.VehicleDto;
import com.logistics.logisticsapp.entity.Vehicle;

public class VehicleMapper {
    private VehicleMapper() {
    }
    public static VehicleDto toDto(Vehicle vehicle) {
        return new VehicleDto(
                vehicle.getId(),
                vehicle.getPlateNumber(),
                vehicle.getModel()
        );
    }
}

