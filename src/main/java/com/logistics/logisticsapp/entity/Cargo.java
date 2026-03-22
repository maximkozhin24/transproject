package com.logistics.logisticsapp.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "cargo")
public class Cargo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private double weight;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    @OneToMany(mappedBy = "cargo", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RouteVehicleCargo> routeVehicleCargoList = new ArrayList<>();

    // Getters и Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public double getWeight() { return weight; }
    public void setWeight(double weight) { this.weight = weight; }

    public Order getOrder() { return order; }
    public void setOrder(Order order) { this.order = order; }

    public List<RouteVehicleCargo> getRouteVehicleCargoList() { return routeVehicleCargoList; }
    public void setRouteVehicleCargoList(List<RouteVehicleCargo> list) { this.routeVehicleCargoList = list; }
}