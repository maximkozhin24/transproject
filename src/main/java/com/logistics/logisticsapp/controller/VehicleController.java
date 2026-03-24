package com.logistics.logisticsapp.controller;

import com.logistics.logisticsapp.dto.AssignVehicleDto;
import com.logistics.logisticsapp.dto.VehicleRequestDto;
import com.logistics.logisticsapp.dto.VehicleResponseDto;
import com.logistics.logisticsapp.service.VehicleService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/vehicles")
public class VehicleController {

    private final VehicleService vehicleService;

    // 🔥 ВАЖНО: конструктор для внедрения
    public VehicleController(VehicleService vehicleService) {
        this.vehicleService = vehicleService;
    }

    // 🔥 CREATE
    @PostMapping
    public ResponseEntity<VehicleResponseDto> create(@RequestBody VehicleRequestDto dto) {
        return ResponseEntity.ok(vehicleService.create(dto));
    }

    // 🔥 ASSIGN VEHICLE
    @PostMapping("/assign")
    public ResponseEntity<String> assignVehicle(@RequestBody AssignVehicleDto dto) {
        vehicleService.assignVehicle(dto);
        return ResponseEntity.ok("Vehicle assigned to order successfully");
    }

    // 🔥 GET ALL
    @GetMapping
    public ResponseEntity<List<VehicleResponseDto>> getAll() {
        return ResponseEntity.ok(vehicleService.getAll());
    }

    // 🔥 GET BY ID
    @GetMapping("/{id}")
    public ResponseEntity<VehicleResponseDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(vehicleService.getById(id));
    }

    // 🔥 UPDATE
    @PutMapping("/{id}")
    public ResponseEntity<VehicleResponseDto> update(@PathVariable Long id,
                                                     @RequestBody VehicleRequestDto dto) {
        return ResponseEntity.ok(vehicleService.update(id, dto));
    }

    // 🔥 DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        vehicleService.delete(id);
        return ResponseEntity.noContent().build();
    }
}