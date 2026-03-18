package com.logistics.logisticsapp.service;

import com.logistics.logisticsapp.dto.CargoRequestDto;
import com.logistics.logisticsapp.dto.CargoResponseDto;
import com.logistics.logisticsapp.entity.Cargo;
import com.logistics.logisticsapp.mapper.CargoMapper;
import com.logistics.logisticsapp.repository.CargoRepository;
import com.logistics.logisticsapp.repository.OrderRepository;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CargoService {

    private final CargoRepository cargoRepository;
    private final OrderRepository orderRepository;

    public CargoService(CargoRepository cargoRepository, OrderRepository orderRepository) {
        this.cargoRepository = cargoRepository;
        this.orderRepository = orderRepository;
    }

    public List<CargoResponseDto> getAll() {
        return cargoRepository.findAll().stream()
            .map(CargoMapper::toDto)
            .collect(Collectors.toList());
    }

    public CargoResponseDto getById(Long id) {
        Cargo cargo = cargoRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Cargo not found"));
        return CargoMapper.toDto(cargo);
    }

    public CargoResponseDto create(CargoRequestDto dto) {
        Cargo cargo = CargoMapper.toEntity(dto);
        cargo.setOrder(orderRepository.findById(dto.getOrderId())
            .orElseThrow(() -> new RuntimeException("Order not found")));
        return CargoMapper.toDto(cargoRepository.save(cargo));
    }

    public CargoResponseDto update(Long id, CargoRequestDto dto) {
        Cargo cargo = cargoRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Cargo not found"));

        cargo.setName(dto.getName());
        cargo.setWeight(dto.getWeight());
        cargo.setOrder(orderRepository.findById(dto.getOrderId())
            .orElseThrow(() -> new RuntimeException("Order not found")));

        return CargoMapper.toDto(cargoRepository.save(cargo));
    }

    public void delete(Long id) {
        cargoRepository.deleteById(id);
    }
}