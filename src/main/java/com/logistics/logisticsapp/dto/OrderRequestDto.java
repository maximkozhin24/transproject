package com.logistics.logisticsapp.dto;

import com.logistics.logisticsapp.entity.OrderStatus;

import java.util.List;

public class OrderRequestDto {

    private double price;
    private OrderStatus status;
    private Long clientId;

    // 🔥 список связей
    private List<RouteVehicleCargoRequestDto> items;

    // ===== getters & setters =====

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

    public List<RouteVehicleCargoRequestDto> getItems() {
        return items;
    }

    public void setItems(List<RouteVehicleCargoRequestDto> items) {
        this.items = items;
    }
}