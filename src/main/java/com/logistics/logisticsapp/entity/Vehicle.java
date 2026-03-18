package com.logistics.logisticsapp.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "vehicles")
public class Vehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String plateNumber;
    private String model;
    private double capacity;

    @OneToMany(mappedBy = "vehicle", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RouteVehicleCargo> routeVehicleCargoList = new ArrayList<>();

    // Getters и Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getPlateNumber() { return plateNumber; }
    public void setPlateNumber(String plateNumber) { this.plateNumber = plateNumber; }

    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }

    public double getCapacity() { return capacity; }
    public void setCapacity(double capacity) { this.capacity = capacity; }

    public List<RouteVehicleCargo> getRouteVehicleCargoList() { return routeVehicleCargoList; }
    public void setRouteVehicleCargoList(List<RouteVehicleCargo> list) { this.routeVehicleCargoList = list; }
}