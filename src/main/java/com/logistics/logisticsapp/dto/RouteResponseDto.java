package com.logistics.logisticsapp.dto;

public class RouteResponseDto {

    private Long id;
    private String startLocation;
    private String endLocation;
    private double distance;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setStartLocation(String startLocation) {
        this.startLocation = startLocation;
    }

    public void setEndLocation(String endLocation) {
        this.endLocation = endLocation;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }
}