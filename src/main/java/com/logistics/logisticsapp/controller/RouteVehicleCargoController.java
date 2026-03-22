package com.logistics.logisticsapp.controller;

import com.logistics.logisticsapp.dto.RouteVehicleCargoRequestDto;
import com.logistics.logisticsapp.dto.RouteVehicleCargoResponseDto;
import com.logistics.logisticsapp.service.RouteVehicleCargoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/rvc")
public class RouteVehicleCargoController {

    private final RouteVehicleCargoService service;

    public RouteVehicleCargoController(RouteVehicleCargoService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<RouteVehicleCargoResponseDto> create(@RequestBody RouteVehicleCargoRequestDto dto) {
        return ResponseEntity.ok(service.create(dto));
    }
}
