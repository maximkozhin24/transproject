package com.logistics.logisticsapp.service;

import com.logistics.logisticsapp.dto.VehicleDto;
import com.logistics.logisticsapp.entity.Vehicle;
import com.logistics.logisticsapp.mapper.VehicleMapper;
import com.logistics.logisticsapp.repository.VehicleRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VehicleService {

    private final VehicleRepository vehicleRepository;
    private final VehicleMapper vehicleMapper; // экземпляр mapper

    public VehicleService(VehicleRepository vehicleRepository, VehicleMapper vehicleMapper) {
        this.vehicleRepository = vehicleRepository;
        this.vehicleMapper = vehicleMapper;
    }

    // Получить все машины
    public List<VehicleDto> getAllVehicles() {
        return vehicleRepository.findAll()
            .stream()
            .map(vehicleMapper::toDto) // используем экземпляр, а не класс
            .toList();
    }

    // Найти машины по модели
    public List<VehicleDto> getVehiclesByModel(String model) {
        return vehicleRepository.findByModel(model)
            .stream()
            .map(vehicleMapper::toDto)
            .toList();
    }

    public VehicleDto getVehicleById(Long id) {
        Vehicle vehicle = vehicleRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Vehicle not found with id: " + id));
        return vehicleMapper.toDto(vehicle);
    }
}

