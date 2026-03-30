package com.logistics.logisticsapp.dto;

import java.util.List;

public class VehicleResponseDto {

    private Long id;
    private String plateNumber;
    private String model;
    private double capacity;
    private List<Long> routeVehicleCargoIds;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setPlateNumber(String plateNumber) {
        this.plateNumber = plateNumber;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public void setCapacity(double capacity) {
        this.capacity = capacity;
    }

    public String getPlateNumber() {
        return plateNumber;
    }
    public String getModel() {
        return model;
    }
    public double getCapacity() {
        return capacity;
    }
}