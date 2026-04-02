package com.logistics.logisticsapp.service;

import com.logistics.logisticsapp.dto.AssignVehicleDto;
import com.logistics.logisticsapp.dto.VehicleRequestDto;
import com.logistics.logisticsapp.dto.VehicleResponseDto;
import com.logistics.logisticsapp.entity.RouteVehicleCargo;
import com.logistics.logisticsapp.entity.Vehicle;
import com.logistics.logisticsapp.mapper.VehicleMapper;
import com.logistics.logisticsapp.repository.RouteVehicleCargoRepository;
import com.logistics.logisticsapp.repository.VehicleRepository;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VehicleService {

    private final VehicleRepository vehicleRepository;
    private final RouteVehicleCargoRepository rvcRepository;

    public VehicleService(VehicleRepository vehicleRepository,
                          RouteVehicleCargoRepository rvcRepository) {
        this.vehicleRepository = vehicleRepository;
        this.rvcRepository = rvcRepository;
    }

    public VehicleResponseDto create(VehicleRequestDto dto) {

        Vehicle vehicle = VehicleMapper.toEntity(dto);
        vehicle = vehicleRepository.save(vehicle);

        return VehicleMapper.toDto(vehicle);
    }

    public List<VehicleResponseDto> getAll() {
        return vehicleRepository.findAll()
            .stream()
            .map(VehicleMapper::toDto)
            .toList();
    }
    private static final String VEHICLE_NOT_FOUND = "Vehicle not found";
    public VehicleResponseDto getById(Long id) {
        Vehicle vehicle = vehicleRepository.findById(id)
            .orElseThrow(() -> new RuntimeException(VEHICLE_NOT_FOUND));

        return VehicleMapper.toDto(vehicle);
    }

    public VehicleResponseDto update(Long id, VehicleRequestDto dto) {
        Vehicle vehicle = vehicleRepository.findById(id)
            .orElseThrow(() -> new RuntimeException(VEHICLE_NOT_FOUND));

        vehicle.setPlateNumber(dto.getPlateNumber());
        vehicle.setModel(dto.getModel());
        vehicle.setCapacity(dto.getCapacity());

        return VehicleMapper.toDto(vehicleRepository.save(vehicle));
    }

    public void assignVehicle(AssignVehicleDto dto) {

        Vehicle vehicle = vehicleRepository.findById(dto.getVehicleId())
            .orElseThrow(() -> new RuntimeException(VEHICLE_NOT_FOUND));

        List<RouteVehicleCargo> relations =
            rvcRepository.findByOrderId(dto.getOrderId());

        if (relations.isEmpty()) {
            throw new IllegalStateException("No relations found for this order");
        }

        for (RouteVehicleCargo rvc : relations) {
            rvc.setVehicle(vehicle);
            rvcRepository.save(rvc);
        }
    }

    public void delete(Long vehicleId) {

        Vehicle vehicle = vehicleRepository.findById(vehicleId)
            .orElseThrow(() -> new RuntimeException(VEHICLE_NOT_FOUND));

        List<RouteVehicleCargo> relations = rvcRepository.findAllByVehicleId(vehicleId);

        for (RouteVehicleCargo rvc : relations) {
            rvc.setVehicle(null);
        }

        vehicleRepository.delete(vehicle);
    }
}