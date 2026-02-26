package com.logistics.logisticsapp.repository;

import com.logistics.logisticsapp.entity.Vehicle;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class VehicleRepository {

    private final List<Vehicle> vehicles = new ArrayList<>();

    public VehicleRepository() {
        vehicles.add(new Vehicle(1L, "AA1234BB", "Volvo FH16", 20000.0));
        vehicles.add(new Vehicle(2L, "BB5678CC", "Scania R500", 18000.0));
    }

    public List<Vehicle> findAll() {
        return vehicles;
    }

    public Optional<Vehicle> findById(Long id) {
        return vehicles.stream()
                .filter(v -> v.getId().equals(id))
                .findFirst();
    }

    public List<Vehicle> findByModel(String model) {
        return vehicles.stream()
                .filter(v -> v.getModel().equalsIgnoreCase(model))
                .toList();
    }
}
