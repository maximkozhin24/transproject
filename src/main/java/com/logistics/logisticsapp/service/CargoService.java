package com.logistics.logisticsapp.service;

import com.logistics.logisticsapp.dto.CargoRequestDto;
import com.logistics.logisticsapp.dto.CargoResponseDto;
import com.logistics.logisticsapp.entity.Cargo;
import com.logistics.logisticsapp.mapper.CargoMapper;
import com.logistics.logisticsapp.repository.CargoRepository;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CargoService {

    private final CargoRepository cargoRepository;

    public CargoService(CargoRepository cargoRepository) {
        this.cargoRepository = cargoRepository;
    }

    // 🔥 CREATE
    public CargoResponseDto create(CargoRequestDto dto) {

        Cargo cargo = new Cargo();
        cargo.setName(dto.getName());
        cargo.setWeight(dto.getWeight());

        cargo = cargoRepository.save(cargo);

        return CargoMapper.toDto(cargo);
    }

    // 🔥 GET ALL
    public List<CargoResponseDto> getAll() {
        return cargoRepository.findAll()
            .stream()
            .map(CargoMapper::toDto)
            .collect(Collectors.toList());
    }

    // 🔥 GET BY ID
    public CargoResponseDto getById(Long id) {
        Cargo cargo = cargoRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Cargo not found"));

        return CargoMapper.toDto(cargo);
    }

    // 🔥 UPDATE
    public CargoResponseDto update(Long id, CargoRequestDto dto) {

        Cargo cargo = cargoRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Cargo not found"));

        cargo.setName(dto.getName());
        cargo.setWeight(dto.getWeight());

        cargo = cargoRepository.save(cargo);

        return CargoMapper.toDto(cargo);
    }

    // 🔥 DELETE
    public void delete(Long id) {
        cargoRepository.deleteById(id);
    }
}