package com.logistics.logisticsapp.controller;

import com.logistics.logisticsapp.dto.CargoRequestDto;
import com.logistics.logisticsapp.dto.CargoResponseDto;
import com.logistics.logisticsapp.dto.TwoCargoRequest;
import com.logistics.logisticsapp.service.CargoService;
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

    @GetMapping
    public ResponseEntity<List<CargoResponseDto>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CargoResponseDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PostMapping
    public ResponseEntity<CargoResponseDto> create(@RequestBody CargoRequestDto dto) {
        return ResponseEntity.ok(service.create(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CargoResponseDto> update(@PathVariable Long id, @RequestBody CargoRequestDto dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/no-transaction")
    public String createNoTransaction(@RequestBody TwoCargoRequest request) {

        service.createTwoCargosNoTransaction(
            request.getFirst(),
            request.getSecond()
        );
        return "Done";
    }

    @PostMapping("/transaction")
    public String createWithTransaction(@RequestBody TwoCargoRequest request) {
        service.createTwoCargosTransactional(
            request.getFirst(),
            request.getSecond()
        );
        return "Done";
    }
}