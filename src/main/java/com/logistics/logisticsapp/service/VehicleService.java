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

    // 🔥 CREATE
    public VehicleResponseDto create(VehicleRequestDto dto) {

        Vehicle vehicle = VehicleMapper.toEntity(dto);
        vehicle = vehicleRepository.save(vehicle);

        return VehicleMapper.toDto(vehicle);
    }

    // 🔥 GET ALL
    public List<VehicleResponseDto> getAll() {
        return vehicleRepository.findAll()
            .stream()
            .map(VehicleMapper::toDto)
            .toList();
    }
final static String err ="Vehicle not found";
    // 🔥 GET BY ID
    public VehicleResponseDto getById(Long id) {
        Vehicle vehicle = vehicleRepository.findById(id)
            .orElseThrow(() -> new RuntimeException(err));

        return VehicleMapper.toDto(vehicle);
    }

    // 🔥 UPDATE
    public VehicleResponseDto update(Long id, VehicleRequestDto dto) {
        Vehicle vehicle = vehicleRepository.findById(id)
            .orElseThrow(() -> new RuntimeException(err));

        vehicle.setPlateNumber(dto.getPlateNumber());
        vehicle.setModel(dto.getModel());
        vehicle.setCapacity(dto.getCapacity());

        return VehicleMapper.toDto(vehicleRepository.save(vehicle));
    }

    public void assignVehicle(AssignVehicleDto dto) {

        Vehicle vehicle = vehicleRepository.findById(dto.getVehicleId())
            .orElseThrow(() -> new RuntimeException(err));

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

    // 🔥 DELETE
    public void delete(Long id) {
        vehicleRepository.deleteById(id);
    }
}