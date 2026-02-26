package com.logistics.logisticsapp.dto;

public class VehicleDto {

    private Long id;
    private String plateNumber;
    private String model;

    public VehicleDto(Long id, String plateNumber, String model) {
        this.id = id;
        this.plateNumber = plateNumber;
        this.model = model;
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
}

