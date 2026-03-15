package com.logistics.logisticsapp.mapper;

import com.logistics.logisticsapp.dto.VehicleDto;
import com.logistics.logisticsapp.entity.Vehicle;
import org.springframework.stereotype.Component;

@Component
public class VehicleMapper {

    // Entity → DTO
    public VehicleDto toDto(Vehicle vehicle) {
        VehicleDto dto = new VehicleDto();

        dto.setId(vehicle.getId());
        dto.setPlateNumber(vehicle.getPlateNumber());
        dto.setModel(vehicle.getModel());
        dto.setCapacity(vehicle.getCapacity());

        return dto;
    }

    // DTO → Entity
    public Vehicle toEntity(VehicleDto dto) {
        Vehicle vehicle = new Vehicle();

        vehicle.setId(dto.getId());
        vehicle.setPlateNumber(dto.getPlateNumber());
        vehicle.setModel(dto.getModel());
        vehicle.setCapacity(dto.getCapacity());

        return vehicle;
    }
}
