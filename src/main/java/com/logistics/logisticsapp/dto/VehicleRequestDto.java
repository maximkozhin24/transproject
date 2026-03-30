package com.logistics.logisticsapp.dto;

public class VehicleRequestDto {

    private String plateNumber;
    private String model;
    private double capacity;

    public String getPlateNumber() {
        return plateNumber;
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
}