package com.logistics.logisticsapp.dto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class VehicleRequestDto {
    @NotBlank(message = "PlateNumber must not be null")
    private String plateNumber;
    @NotBlank(message = "Model must not be null")
    private String model;
    @NotNull(message = "Capacity must not be null")
    @Positive(message = "Capacity must be positive")
    private Double capacity;

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