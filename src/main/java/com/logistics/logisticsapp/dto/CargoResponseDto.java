package com.logistics.logisticsapp.dto;

import java.util.List;

public class CargoResponseDto {

    private Long id;
    private String name;
    private double weight;
    private Long orderId;
    private List<Long> routeVehicleCargoIds;

    public CargoResponseDto() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public List<Long> getRouteVehicleCargoIds() {
        return routeVehicleCargoIds;
    }

    public void setRouteVehicleCargoIds(List<Long> routeVehicleCargoIds) {
        this.routeVehicleCargoIds = routeVehicleCargoIds;
    }
}