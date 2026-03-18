package com.logistics.logisticsapp.service;

import com.logistics.logisticsapp.dto.VehicleRequestDto;
import com.logistics.logisticsapp.dto.VehicleResponseDto;
import com.logistics.logisticsapp.entity.Vehicle;
import com.logistics.logisticsapp.mapper.VehicleMapper;
import com.logistics.logisticsapp.repository.VehicleRepository;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class VehicleService {

    private final VehicleRepository repository;

    public VehicleService(VehicleRepository repository) {
        this.repository = repository;
    }

    public List<VehicleResponseDto> getAll() {
        return repository.findAll().stream()
            .map(VehicleMapper::toDto)
            .collect(Collectors.toList());
    }

    public VehicleResponseDto getById(Long id) {
        Vehicle vehicle = repository.findById(id)
            .orElseThrow(() -> new RuntimeException("Vehicle not found"));
        return VehicleMapper.toDto(vehicle);
    }

    public VehicleResponseDto create(VehicleRequestDto dto) {
        Vehicle vehicle = VehicleMapper.toEntity(dto);
        return VehicleMapper.toDto(repository.save(vehicle));
    }

    public VehicleResponseDto update(Long id, VehicleRequestDto dto) {
        Vehicle vehicle = repository.findById(id)
            .orElseThrow(() -> new RuntimeException("Vehicle not found"));

        vehicle.setPlateNumber(dto.getPlateNumber());
        vehicle.setModel(dto.getModel());
        vehicle.setCapacity(dto.getCapacity());

        return VehicleMapper.toDto(repository.save(vehicle));
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }
}