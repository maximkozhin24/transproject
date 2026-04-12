package com.logistics.logisticsapp.controller;

import com.logistics.logisticsapp.dto.ClientRequestDto;
import com.logistics.logisticsapp.dto.ClientResponseDto;
import com.logistics.logisticsapp.service.ClientService;

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
@RequestMapping("/api/clients")
public class ClientController {

    private final ClientService clientService;

    public ClientController(ClientService clientService) {
        this.clientService = clientService;
    }

    @Operation(
        summary = "Получить всех клиентов",
        description = "Возвращает список всех клиентов"
    )
    @GetMapping
    public ResponseEntity<List<ClientResponseDto>> getAllClients() {
        return ResponseEntity.ok(clientService.getAll());
    }

    @Operation(
        summary = "Получить клиента по ID",
        description = "Возвращает клиента по его идентификатору"
    )
    @GetMapping("/{id}")
    public ResponseEntity<ClientResponseDto> getClientById(@PathVariable Long id) {
        return ResponseEntity.ok(clientService.getById(id));
    }

    @Operation(
        summary = "Создать клиента",
        description = "Создаёт нового клиента"
    )
    @PostMapping
    public ResponseEntity<ClientResponseDto> createClient(@Valid @RequestBody ClientRequestDto dto) {
        return ResponseEntity.ok(clientService.create(dto));
    }

    @Operation(
        summary = "Обновить клиента",
        description = "Обновляет существующего клиента по ID"
    )
    @PutMapping("/{id}")
    public ResponseEntity<ClientResponseDto> updateClient(
        @PathVariable Long id,
        @RequestBody ClientRequestDto dto) {
        return ResponseEntity.ok(clientService.update(id, dto));
    }

    @Operation(
        summary = "Удалить клиента",
        description = "Удаляет клиента по ID"
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteClient(@PathVariable Long id) {
        clientService.delete(id);
        return ResponseEntity.noContent().build();
    }
}