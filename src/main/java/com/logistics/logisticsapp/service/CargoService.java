package com.logistics.logisticsapp.service;

import com.logistics.logisticsapp.dto.CargoRequestDto;
import com.logistics.logisticsapp.dto.CargoResponseDto;
import com.logistics.logisticsapp.entity.Cargo;
import com.logistics.logisticsapp.mapper.CargoMapper;
import com.logistics.logisticsapp.repository.CargoRepository;

import jakarta.transaction.Transactional;
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
            .toList();
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

    // ❌ БЕЗ ТРАНЗАКЦИИ
    public void createTwoCargosNoTransaction(CargoRequestDto dto1, CargoRequestDto dto2) {
        createTwoCargos(dto1, dto2, false);
    }

    // ✅ С ТРАНЗАКЦИЕЙ
    @Transactional
    public void createTwoCargosTransactional(CargoRequestDto dto1, CargoRequestDto dto2) {
        createTwoCargos(dto1, dto2, true);
    }

    // 🔥 приватный метод с общей логикой
    private void createTwoCargos(CargoRequestDto dto1, CargoRequestDto dto2, boolean transactional) {

        // сохраняем первый cargo
        Cargo cargo1 = CargoMapper.toEntity(dto1);
        cargoRepository.save(cargo1);

        // сохраняем второй cargo
        Cargo cargo2 = CargoMapper.toEntity(dto2);

        // искусственная ошибка на втором cargo
        if (true) {
            throw new IllegalStateException("Ошибка при сохранении второго cargo");
        }

        cargoRepository.save(cargo2);
    }
}