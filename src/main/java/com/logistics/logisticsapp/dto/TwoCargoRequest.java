package com.logistics.logisticsapp.dto;

public class TwoCargoRequest {

    private CargoRequestDto first;
    private CargoRequestDto second;

    public CargoRequestDto getFirst() {
        return first;
    }

    public void setFirst(CargoRequestDto first) {
        this.first = first;
    }

    public CargoRequestDto getSecond() {
        return second;
    }

    public void setSecond(CargoRequestDto second) {
        this.second = second;
    }
}