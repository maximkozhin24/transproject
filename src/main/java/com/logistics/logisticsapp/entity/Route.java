package com.logistics.logisticsapp.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "routes")
public class Route {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String startLocation;
    private String endLocation;
    private double distance;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;

    @OneToMany(mappedBy = "route", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RouteVehicleCargo> routeVehicleCargoList = new ArrayList<>();

    // Getters и Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getStartLocation() { return startLocation; }
    public void setStartLocation(String startLocation) { this.startLocation = startLocation; }

    public String getEndLocation() { return endLocation; }
    public void setEndLocation(String endLocation) { this.endLocation = endLocation; }

    public double getDistance() { return distance; }
    public void setDistance(double distance) { this.distance = distance; }

    public Order getOrder() { return order; }
    public void setOrder(Order order) { this.order = order; }

    public List<RouteVehicleCargo> getRouteVehicleCargoList() { return routeVehicleCargoList; }
    public void setRouteVehicleCargoList(List<RouteVehicleCargo> list) { this.routeVehicleCargoList = list; }
}