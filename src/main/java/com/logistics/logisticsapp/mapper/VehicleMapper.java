package com.logistics.logisticsapp.mapper;

import com.logistics.logisticsapp.dto.VehicleRequestDto;
import com.logistics.logisticsapp.dto.VehicleResponseDto;
import com.logistics.logisticsapp.entity.Vehicle;

public class VehicleMapper {
private VehicleMapper(){}
    public static Vehicle toEntity(VehicleRequestDto dto) {
        Vehicle vehicle = new Vehicle();

        vehicle.setPlateNumber(dto.getPlateNumber());
        vehicle.setModel(dto.getModel());
        vehicle.setCapacity(dto.getCapacity());

        return vehicle;
    }

    public static VehicleResponseDto toDto(Vehicle vehicle) {
        VehicleResponseDto dto = new VehicleResponseDto();

        dto.setId(vehicle.getId());
        dto.setPlateNumber(vehicle.getPlateNumber());
        dto.setModel(vehicle.getModel());
        dto.setCapacity(vehicle.getCapacity());

        return dto;
    }
}