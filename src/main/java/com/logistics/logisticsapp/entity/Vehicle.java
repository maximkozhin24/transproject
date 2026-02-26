package com.logistics.logisticsapp.entity;

public class Vehicle {

    private Long id;
    private String plateNumber;
    private String model;
    private Double capacity;

    public Vehicle(Long id, String plateNumber, String model, Double capacity) {
        this.id = id;
        this.plateNumber = plateNumber;
        this.model = model;
        this.capacity = capacity;
    }

    public Long getId() {
        return id;
    }

    public String getPlateNumber() {
        return plateNumber;
    }

    public String getModel() {
        return model;
    }

    public Double getCapacity() {
        return capacity;
    }
}

