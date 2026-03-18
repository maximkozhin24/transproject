package com.logistics.logisticsapp.controller;

import com.logistics.logisticsapp.dto.ClientRequestDto;
import com.logistics.logisticsapp.dto.ClientResponseDto;
import com.logistics.logisticsapp.service.ClientService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clients")
public class ClientController {

    private final ClientService clientService;

    public ClientController(ClientService clientService) {
        this.clientService = clientService;
    }

    // GET ALL
    @GetMapping
    public ResponseEntity<List<ClientResponseDto>> getAllClients() {
        return ResponseEntity.ok(clientService.getAll());
    }

    // GET BY ID
    @GetMapping("/{id}")
    public ResponseEntity<ClientResponseDto> getClientById(@PathVariable Long id) {
        return ResponseEntity.ok(clientService.getById(id));
    }

    // POST (CREATE)
    @PostMapping
    public ResponseEntity<ClientResponseDto> createClient(@RequestBody ClientRequestDto dto) {
        return ResponseEntity.ok(clientService.create(dto));
    }

    //  PUT (UPDATE)
    @PutMapping("/{id}")
    public ResponseEntity<ClientResponseDto> updateClient(
        @PathVariable Long id,
        @RequestBody ClientRequestDto dto) {
        return ResponseEntity.ok(clientService.update(id, dto));
    }

    // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteClient(@PathVariable Long id) {
        clientService.delete(id);
        return ResponseEntity.noContent().build();
    }
}