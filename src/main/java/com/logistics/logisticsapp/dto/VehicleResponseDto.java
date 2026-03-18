package com.logistics.logisticsapp.dto;

import java.util.List;

public class VehicleResponseDto {

    private Long id;
    private String plateNumber;
    private String model;
    private double capacity;
    private List<Long> routeVehicleCargoIds;

    public VehicleResponseDto() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPlateNumber() {
        return plateNumber;
    }

    public void setPlateNumber(String plateNumber) {
        this.plateNumber = plateNumber;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public double getCapacity() {
        return capacity;
    }

    public void setCapacity(double capacity) {
        this.capacity = capacity;
    }

    public List<Long> getRouteVehicleCargoIds() {
        return routeVehicleCargoIds;
    }

    public void setRouteVehicleCargoIds(List<Long> routeVehicleCargoIds) {
        this.routeVehicleCargoIds = routeVehicleCargoIds;
    }
}