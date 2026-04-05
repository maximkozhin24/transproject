package com.logistics.logisticsapp.cache;

import java.util.Objects;

public class OrderSearchKey {

    private final String cargoName;

    public OrderSearchKey(String cargoName) {
        this.cargoName = cargoName;
    }

    public String getCargoName() {
        return cargoName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OrderSearchKey)) return false;
        OrderSearchKey that = (OrderSearchKey) o;
        return Objects.equals(cargoName, that.cargoName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cargoName);
    }
}