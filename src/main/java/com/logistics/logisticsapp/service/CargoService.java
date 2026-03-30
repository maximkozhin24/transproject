package com.logistics.logisticsapp.service;

import com.logistics.logisticsapp.dto.CargoRequestDto;
import com.logistics.logisticsapp.dto.CargoResponseDto;
import com.logistics.logisticsapp.entity.Cargo;
import com.logistics.logisticsapp.entity.Order;
import com.logistics.logisticsapp.entity.RouteVehicleCargo;
import com.logistics.logisticsapp.mapper.CargoMapper;
import com.logistics.logisticsapp.repository.CargoRepository;

import com.logistics.logisticsapp.repository.OrderRepository;
import com.logistics.logisticsapp.repository.RouteVehicleCargoRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CargoService {

    private final CargoRepository cargoRepository;
    private final OrderRepository orderRepository;
    private final RouteVehicleCargoRepository rvcRepository;
    public CargoService(CargoRepository cargoRepository, OrderRepository orderRepository,
                        RouteVehicleCargoRepository rvcRepository) {
        this.cargoRepository = cargoRepository;
        this.orderRepository = orderRepository;
        this.rvcRepository = rvcRepository;
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
    static final String ERROR_CARGO = "Cargo not found";
    // 🔥 GET BY ID
    public CargoResponseDto getById(Long id) {
        Cargo cargo = cargoRepository.findById(id)
            .orElseThrow(() -> new RuntimeException(ERROR_CARGO));

        return CargoMapper.toDto(cargo);
    }

    // 🔥 UPDATE
    public CargoResponseDto update(Long id, CargoRequestDto dto) {

        Cargo cargo = cargoRepository.findById(id)
            .orElseThrow(() -> new RuntimeException(ERROR_CARGO));

        cargo.setName(dto.getName());
        cargo.setWeight(dto.getWeight());

        cargo = cargoRepository.save(cargo);

        return CargoMapper.toDto(cargo);
    }

    // 🔥 DELETE
    public void delete(Long cargoId) {
        Cargo cargo = cargoRepository.findById(cargoId)
            .orElseThrow(() -> new RuntimeException(ERROR_CARGO));

        // Удаляем все связи
        List<RouteVehicleCargo> relations = rvcRepository.findAllByCargo_Id(cargoId);
        rvcRepository.deleteAll(relations);

        // Проверяем, остались ли Order без Cargo и Route
        for (RouteVehicleCargo rvc : relations) {
            Order order = rvc.getOrder();
            List<RouteVehicleCargo> remaining = rvcRepository.findAllByOrder_Id(order.getId());
            if (remaining.isEmpty()) {
                orderRepository.delete(order);
            }
        }

        // Удаляем Cargo
        cargoRepository.delete(cargo);
    }

    // ❌ БЕЗ ТРАНЗАКЦИИ
    public void createTwoCargosNoTransaction(CargoRequestDto dto1, CargoRequestDto dto2) {
        createTwoCargos(dto1, dto2);
    }

    // ✅ С ТРАНЗАКЦИЕЙ
    @Transactional
    public void createTwoCargosTransactional(CargoRequestDto dto1, CargoRequestDto dto2) {
        createTwoCargos(dto1, dto2);
    }

    // 🔥 приватный метод с общей логикой
    private void createTwoCargos(CargoRequestDto dto1, CargoRequestDto dto2) {

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