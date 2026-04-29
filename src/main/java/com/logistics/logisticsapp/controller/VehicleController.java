package com.logistics.logisticsapp.controller;

import com.logistics.logisticsapp.dto.AssignVehicleDto;
import com.logistics.logisticsapp.dto.RaceConditionReport;
import com.logistics.logisticsapp.dto.VehicleRequestDto;
import com.logistics.logisticsapp.dto.VehicleResponseDto;
import com.logistics.logisticsapp.entity.TaskStatus;
import com.logistics.logisticsapp.service.VehicleService;

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
@RequestMapping("/vehicles")
public class VehicleController {

    private final VehicleService vehicleService;

    public VehicleController(VehicleService vehicleService) {
        this.vehicleService = vehicleService;
    }

    @Operation(
        summary = "Создать машину",
        description = "Создаёт новую машину"
    )
    @PostMapping
    public ResponseEntity<VehicleResponseDto> create(@Valid @RequestBody VehicleRequestDto dto) {
        return ResponseEntity.ok(vehicleService.create(dto));
    }

    @Operation(
        summary = "Привязка машины к заказу",
        description = "Привязывает машину к существующему заказу"
    )
    @PostMapping("/assign")
    public ResponseEntity<String> assignVehicle(@Valid @RequestBody AssignVehicleDto dto) {
        vehicleService.assignVehicle(dto);
        return ResponseEntity.ok("Vehicle assigned to order successfully");
    }

    @Operation(
        summary = "Получить все машины",
        description = "Возвращает список всех машин"
    )
    @GetMapping
    public ResponseEntity<List<VehicleResponseDto>> getAll() {
        return ResponseEntity.ok(vehicleService.getAll());
    }

    @Operation(
        summary = "Получить машину по ID",
        description = "Возвращает машину по его идентификатору"
    )
    @GetMapping("/{id}")
    public ResponseEntity<VehicleResponseDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(vehicleService.getById(id));
    }

    @Operation(
        summary = "Обновить машину",
        description = "Обновляет существующую машину по ID"
    )
    @PutMapping("/{id}")
    public ResponseEntity<VehicleResponseDto> update(@PathVariable Long id,
                                                     @RequestBody VehicleRequestDto dto) {
        return ResponseEntity.ok(vehicleService.update(id, dto));
    }

    @Operation(
        summary = "Удалить машину",
        description = "Удаляет машину по ID"
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        vehicleService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/async")
    public ResponseEntity<String> getAllAsync() {
        return ResponseEntity.ok(vehicleService.getAllAsync());
    }

    @GetMapping("/async/status/{taskId}")
    public ResponseEntity<TaskStatus> getStatus(@PathVariable String taskId) {
        return ResponseEntity.ok(vehicleService.getStatus(taskId));
    }

    @GetMapping("/async/result/{taskId}")
    public ResponseEntity<List<VehicleResponseDto>> getResult(@PathVariable String taskId) {
        return ResponseEntity.ok(vehicleService.getResult(taskId));
    }

    @GetMapping("/race-condition/bad")
    public ResponseEntity<RaceConditionReport> runBadRaceCondition() {
        return ResponseEntity.ok(vehicleService.runBadRaceConditionDemo());
    }

    @GetMapping("/race-condition/fixed")
    public ResponseEntity<RaceConditionReport> runFixedRaceCondition() {
        return ResponseEntity.ok(vehicleService.runFixedRaceConditionDemo());
    }
}