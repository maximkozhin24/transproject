package com.logistics.logisticsapp.controller;

import com.logistics.logisticsapp.dto.CargoRequestDto;
import com.logistics.logisticsapp.dto.CargoResponseDto;
import com.logistics.logisticsapp.dto.TwoCargoRequest;
import com.logistics.logisticsapp.service.CargoService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/cargo")
public class CargoController {
    private final CargoService service;

    public CargoController(CargoService service) {
        this.service = service;
    }

    @Operation(
        summary = "Получить все товары",
        description = "Возвращает список всех товаров"
    )
    @GetMapping
    public ResponseEntity<List<CargoResponseDto>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @Operation(
        summary = "Получить товар по ID",
        description = "Возвращает товар по его идентификатору"
    )
    @GetMapping("/{id}")
    public ResponseEntity<CargoResponseDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @Operation(
        summary = "Создать товар",
        description = "Создаёт новый товар"
    )
    @PostMapping
    public ResponseEntity<CargoResponseDto> create(@Valid @RequestBody CargoRequestDto dto) {
        return ResponseEntity.ok(service.create(dto));
    }

    @Operation(
        summary = "Обновить товар",
        description = "Обновляет существующий товар по ID"
    )
    @PutMapping("/{id}")
    public ResponseEntity<CargoResponseDto> update(@PathVariable Long id, @RequestBody CargoRequestDto dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    @Operation(
        summary = "Удалить товар",
        description = "Удаляет товар по ID"
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(
        summary = "Создать несколько товаров",
        description = "Создаёт несколько товаров без транзакции"
    )
    @PostMapping("/no-transaction")
    public String createNoTransaction(@Valid @RequestBody TwoCargoRequest request) {

        service.createTwoCargosNoTransaction(
            request.getFirst(),
            request.getSecond()
        );
        return "Done";
    }

    @Operation(
        summary = "Создать несколько товаров",
        description = "Создаёт несколько товаров с транзакции"
    )
    @PostMapping("/transaction")
    public String createWithTransaction(@Valid @RequestBody TwoCargoRequest request) {
        service.createTwoCargosTransactional(
            request.getFirst(),
            request.getSecond()
        );
        return "Done";
    }
}