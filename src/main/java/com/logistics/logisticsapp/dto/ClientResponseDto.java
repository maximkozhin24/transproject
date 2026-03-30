package com.logistics.logisticsapp.dto;

import java.util.List;

public class ClientResponseDto {

    private Long id;
    private String name;
    private String email;
    private String phone;

    private List<OrderResponseDto> orders;

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public List<OrderResponseDto> getOrders() {
        return orders;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setOrders(List<OrderResponseDto> orders) {
        this.orders = orders;
    }
}