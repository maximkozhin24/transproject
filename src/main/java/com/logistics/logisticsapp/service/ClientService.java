package com.logistics.logisticsapp.service;

import com.logistics.logisticsapp.dto.ClientRequestDto;
import com.logistics.logisticsapp.dto.ClientResponseDto;
import com.logistics.logisticsapp.entity.Client;
import com.logistics.logisticsapp.mapper.ClientMapper;
import com.logistics.logisticsapp.repository.ClientRepository;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ClientService {

    private final ClientRepository repository;

    public ClientService(ClientRepository repository) {
        this.repository = repository;
    }

    public List<ClientResponseDto> getAll() {
        return repository.findAll()
            .stream()
            .map(ClientMapper::toDto)
            .toList();
    }

    public ClientResponseDto getById(Long id) {
        Client client = repository.findById(id)
            .orElseThrow(() -> new RuntimeException("Client not found"));

        return ClientMapper.toDto(client);
    }

    public ClientResponseDto create(ClientRequestDto dto) {
        Client client = ClientMapper.toEntity(dto);
        return ClientMapper.toDto(repository.save(client));
    }

    public ClientResponseDto update(Long id, ClientRequestDto dto) {
        Client client = repository.findById(id)
            .orElseThrow(() -> new RuntimeException("Client not found"));

        client.setName(dto.getName());
        client.setEmail(dto.getEmail());
        client.setPhone(dto.getPhone());

        return ClientMapper.toDto(repository.save(client));
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }
}