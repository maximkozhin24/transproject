package com.logistics.logisticsapp.controller;

import com.logistics.logisticsapp.dto.OrderRequestDto;
import com.logistics.logisticsapp.dto.OrderResponseDto;
import com.logistics.logisticsapp.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/orders")
@Tag(name = "Orders", description = "Управление заказами")
public class OrderController {

    private final OrderService service;

    public OrderController(OrderService service) {
        this.service = service;
    }
    @Operation(
        summary = "Получить все заказы",
        description = "Возвращает список всех заказов"
    )
    @GetMapping
    public ResponseEntity<Page<OrderResponseDto>> getAll(@ParameterObject Pageable pageable) {
        return ResponseEntity.ok(service.getAll(pageable));
    }

    @Operation(
        summary = "Получить заказ по ID",
        description = "Возвращает заказ по его идентификатору"
    )
    @GetMapping("/{id}")
    public ResponseEntity<OrderResponseDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @Operation(
        summary = "Получить все заказы оптимизировано",
        description = "Возвращает список всех заказов без n+1"
    )
    @GetMapping("/optimized")
    public List<OrderResponseDto> getAllOptimized() {
        return service.getAllOptimized();
    }

    @Operation(
        summary = "Получить все заказы по товару",
        description = "Возвращает список всех заказов по товару"
    )
    @GetMapping("/by-cargo")
    public List<OrderResponseDto> getByCargo(@RequestParam String cargoName) {
        return service.getOrdersByCargo(cargoName);
    }

    @Operation(
        summary = "Получить все заказы по товару",
        description = "Возвращает список всех заказов с помощью native query"
    )
    @GetMapping("/by-cargo-native")
    public List<OrderResponseDto> getByCargoNative(@RequestParam String cargoName) {
        return service.getOrdersByCargoNative(cargoName);
    }

    @Operation(
        summary = "Получить все заказы по товару",
        description = "Возвращает список всех заказов(запрос кэширован)"
    )
    @GetMapping("/by-cargo-cached")
    public List<OrderResponseDto> getByCargoCached(@RequestParam String cargoName) {
        return service.getOrdersByCargoCached(cargoName);
    }

    @Operation(
        summary = "Создать заказ",
        description = "Создаёт новый заказ с привязкой к клиенту, cargo и route"
    )
    @PostMapping
    public ResponseEntity<OrderResponseDto> create(@Valid @RequestBody OrderRequestDto dto) {
        return ResponseEntity.ok(service.create(dto));
    }

    @Operation(
        summary = "Обновить заказ",
        description = "Обновляет существующий заказ по ID"
    )
    @PutMapping("/{id}")
    public ResponseEntity<OrderResponseDto> update(@PathVariable Long id, @RequestBody OrderRequestDto dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    @Operation(
        summary = "Удалить заказ",
        description = "Удаляет заказ по ID"
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}