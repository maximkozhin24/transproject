package com.logistics.logisticsapp.dto;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class RaceConditionReport {

    private int expected;
    private int actual;
    private int lost;
    private double lossPercent;

    public RaceConditionReport(int expected, int actual) {
        this.expected = expected;
        this.actual = actual;
        this.lost = expected - actual;
        if (expected == 0) {
            this.lossPercent = 0.0;
        } else {
            this.lossPercent = BigDecimal.valueOf((double) lost * 100 / expected)
                .setScale(2, RoundingMode.HALF_UP)
                .doubleValue();
        }
    }

    public int getExpected() {
        return expected; }
    public int getActual() {
        return actual; }
    public int getLost() {
        return lost; }
    public double getLossPercent() {
        return lossPercent; }
}