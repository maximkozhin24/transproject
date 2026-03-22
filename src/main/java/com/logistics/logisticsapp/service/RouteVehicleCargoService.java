package com.logistics.logisticsapp.service;

import com.logistics.logisticsapp.dto.RouteVehicleCargoRequestDto;
import com.logistics.logisticsapp.dto.RouteVehicleCargoResponseDto;
import com.logistics.logisticsapp.entity.RouteVehicleCargo;
import com.logistics.logisticsapp.mapper.RouteVehicleCargoMapper;
import com.logistics.logisticsapp.repository.*;

import org.springframework.stereotype.Service;

@Service
public class RouteVehicleCargoService {

    private final RouteVehicleCargoRepository repository;
    private final RouteRepository routeRepository;
    private final VehicleRepository vehicleRepository;
    private final CargoRepository cargoRepository;

    public RouteVehicleCargoService(RouteVehicleCargoRepository repository,
                                    RouteRepository routeRepository,
                                    VehicleRepository vehicleRepository,
                                    CargoRepository cargoRepository) {
        this.repository = repository;
        this.routeRepository = routeRepository;
        this.vehicleRepository = vehicleRepository;
        this.cargoRepository = cargoRepository;
    }

    public RouteVehicleCargoResponseDto create(RouteVehicleCargoRequestDto dto) {
        RouteVehicleCargo rvc = new RouteVehicleCargo();

        rvc.setRoute(routeRepository.findById(dto.getRouteId()).orElseThrow());
        rvc.setVehicle(vehicleRepository.findById(dto.getVehicleId()).orElseThrow());
        rvc.setCargo(cargoRepository.findById(dto.getCargoId()).orElseThrow());

        return RouteVehicleCargoMapper.toDto(repository.save(rvc));
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }
}