package com.logistics.logisticsapp.service;

import com.logistics.logisticsapp.dto.VehicleDto;
import com.logistics.logisticsapp.mapper.VehicleMapper;
import com.logistics.logisticsapp.repository.VehicleRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VehicleService {

    private final VehicleRepository repository;

    public VehicleService(VehicleRepository repository) {
        this.repository = repository;
    }

    public List<VehicleDto> getAllVehicles() {
        return repository.findAll()
                .stream()
                .map(VehicleMapper::toDto)
                .toList();
    }

    public VehicleDto getVehicleById(Long id) {
        return repository.findById(id)
                .map(VehicleMapper::toDto)
                .orElseThrow(() -> new RuntimeException("Vehicle not found"));
    }

    public List<VehicleDto> getVehiclesByModel(String model) {
        return repository.findByModel(model)
                .stream()
                .map(VehicleMapper::toDto)
                .toList();
    }
}

