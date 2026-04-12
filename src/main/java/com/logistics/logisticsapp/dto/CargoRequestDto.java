package com.logistics.logisticsapp.dto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class CargoRequestDto {
    @NotBlank(message = "Name must not be null")
    private String name;
    @NotNull(message = "Weight must not be null")
    @Positive(message = "Weight must be positive")
    private Double weight;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }
}