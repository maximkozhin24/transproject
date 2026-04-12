package com.logistics.logisticsapp.dto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class RouteRequestDto {
    @NotBlank(message = "StartLocation must not be null")
    private String startLocation;
    @NotBlank(message = "EndLocation must not be null")
    private String endLocation;
    @NotNull(message = "Distance must not be null")
    @Positive(message = "Distance must be positive")
    private Double distance;

    public String getStartLocation() {
        return startLocation;
    }

    public void setStartLocation(String startLocation) {
        this.startLocation = startLocation;
    }

    public String getEndLocation() {
        return endLocation;
    }

    public void setEndLocation(String endLocation) {
        this.endLocation = endLocation;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }
}