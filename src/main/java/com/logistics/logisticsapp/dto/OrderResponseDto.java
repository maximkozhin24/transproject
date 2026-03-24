package com.logistics.logisticsapp.dto;

import com.logistics.logisticsapp.entity.OrderStatus;

import java.util.List;

public class OrderResponseDto {

    private Long id;
    private double price;
    private OrderStatus status;
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

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
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