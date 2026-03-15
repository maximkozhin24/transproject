package com.logistics.logisticsapp.dto;

import java.util.List;

public class RouteDto {

    private Long id;
    private String startLocation;
    private String endLocation;
    private double distance;

    private Long orderId;

    private List<Long> vehicleIds;
    private List<Long> cargoIds;

    public RouteDto() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public List<Long> getVehicleIds() {
        return vehicleIds;
    }

    public void setVehicleIds(List<Long> vehicleIds) {
        this.vehicleIds = vehicleIds;
    }

    public List<Long> getCargoIds() {
        return cargoIds;
    }

    public void setCargoIds(List<Long> cargoIds) {
        this.cargoIds = cargoIds;
    }
}
