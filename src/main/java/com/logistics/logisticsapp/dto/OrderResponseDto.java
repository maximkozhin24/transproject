package com.logistics.logisticsapp.dto;

import java.util.List;

public class OrderResponseDto {

    private Long id;
    private double price;
    private String status;
    private Long clientId;
    private List<Long> routeIds;
    private List<Long> cargoIds;

    public OrderResponseDto() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getClientId() {
        return clientId;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }

    public List<Long> getRouteIds() {
        return routeIds;
    }

    public void setRouteIds(List<Long> routeIds) {
        this.routeIds = routeIds;
    }

    public List<Long> getCargoIds() {
        return cargoIds;
    }

    public void setCargoIds(List<Long> cargoIds) {
        this.cargoIds = cargoIds;
    }
}