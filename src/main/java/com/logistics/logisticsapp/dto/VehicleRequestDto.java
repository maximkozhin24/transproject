package com.logistics.logisticsapp.dto;

public class VehicleRequestDto {

    private String plateNumber;
    private String model;
    private double capacity;

    public VehicleRequestDto() {}

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
}