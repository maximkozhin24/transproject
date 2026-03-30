package com.logistics.logisticsapp.service;

import com.logistics.logisticsapp.dto.ClientRequestDto;
import com.logistics.logisticsapp.dto.ClientResponseDto;
import com.logistics.logisticsapp.entity.Cargo;
import com.logistics.logisticsapp.entity.Client;
import com.logistics.logisticsapp.entity.Order;
import com.logistics.logisticsapp.entity.RouteVehicleCargo;
import com.logistics.logisticsapp.mapper.ClientMapper;
import com.logistics.logisticsapp.repository.CargoRepository;
import com.logistics.logisticsapp.repository.ClientRepository;

import com.logistics.logisticsapp.repository.OrderRepository;
import com.logistics.logisticsapp.repository.RouteVehicleCargoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClientService {

    private final ClientRepository repository;
    private final OrderRepository orderRepository;
    private final CargoRepository cargoRepository;
    private final RouteVehicleCargoRepository rvcRepository;

    public ClientService(ClientRepository repository, OrderRepository orderRepository,
                         CargoRepository cargoRepository, RouteVehicleCargoRepository rvcRepository) {
        this.repository = repository;
        this.orderRepository = orderRepository;
        this.cargoRepository = cargoRepository;
        this.rvcRepository = rvcRepository;
    }

    public List<ClientResponseDto> getAll() {
        return repository.findAll()
            .stream()
            .map(ClientMapper::toDto)
            .toList();
    }
    static final String ERROR_CLIENT = "Client not found";
    public ClientResponseDto getById(Long id) {
        Client client = repository.findById(id)
            .orElseThrow(() -> new RuntimeException(ERROR_CLIENT));

        return ClientMapper.toDto(client);
    }

    public ClientResponseDto create(ClientRequestDto dto) {
        Client client = ClientMapper.toEntity(dto);
        return ClientMapper.toDto(repository.save(client));
    }

    public ClientResponseDto update(Long id, ClientRequestDto dto) {
        Client client = repository.findById(id)
            .orElseThrow(() -> new RuntimeException(ERROR_CLIENT));

        client.setName(dto.getName());
        client.setEmail(dto.getEmail());
        client.setPhone(dto.getPhone());

        return ClientMapper.toDto(repository.save(client));
    }

    public void delete(Long clientId) {
        Client client = repository.findById(clientId)
            .orElseThrow(() -> new RuntimeException(ERROR_CLIENT));

        List<Order> orders = orderRepository.findAllByClientId(clientId);

        for (Order order : orders) {

            List<RouteVehicleCargo> relations = rvcRepository.findAllByOrderId(order.getId());

            rvcRepository.deleteAll(relations);

            for (RouteVehicleCargo rvc : relations) {
                Cargo cargo = rvc.getCargo();
                boolean usedElsewhere = rvcRepository.existsByCargoId(cargo.getId());
                if (!usedElsewhere) {
                    cargoRepository.delete(cargo);
                }
            }

            orderRepository.delete(order);
        }

        repository.delete(client);
    }
}