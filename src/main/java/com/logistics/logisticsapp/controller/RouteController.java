package com.logistics.logisticsapp.controller;

import com.logistics.logisticsapp.dto.RouteRequestDto;
import com.logistics.logisticsapp.dto.RouteResponseDto;
import com.logistics.logisticsapp.service.RouteService;
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
@RequestMapping("/api/routes")
public class RouteController {

    private final RouteService service;

    public RouteController(RouteService service) {
        this.service = service;
    }

    @Operation(
        summary = "Получить все маршруты",
        description = "Возвращает список всех маршрутов"
    )
    @GetMapping
    public ResponseEntity<List<RouteResponseDto>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @Operation(
        summary = "Получить маршурт по ID",
        description = "Возвращает маршрут по его идентификатору"
    )
    @GetMapping("/{id}")
    public ResponseEntity<RouteResponseDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @Operation(
        summary = "Создать маршрут",
        description = "Создаёт новый маршрут"
    )
    @PostMapping
    public ResponseEntity<RouteResponseDto> create(@Valid @RequestBody RouteRequestDto dto) {
        return ResponseEntity.ok(service.create(dto));
    }

    @Operation(
        summary = "Обновить маршрут",
        description = "Обновляет существующий маршрут по ID"
    )
    @PutMapping("/{id}")
    public ResponseEntity<RouteResponseDto> update(@PathVariable Long id, @RequestBody RouteRequestDto dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    @Operation(
        summary = "Удалить маршрут",
        description = "Удаляет маршрут по ID"
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}