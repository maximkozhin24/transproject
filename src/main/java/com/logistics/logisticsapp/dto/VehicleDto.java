package com.logistics.logisticsapp.dto;

public class VehicleDto {

    private Long id;
    private String plateNumber;
    private String model;
    private double capacity;

    public VehicleDto() {
    }

    // -------- GETTERS --------

    public Long getId() {
        return id;
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

    // -------- SETTERS --------

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
}

