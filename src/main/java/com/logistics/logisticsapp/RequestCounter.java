package com.logistics.logisticsapp;

import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicInteger;

@Component
public class RequestCounter {

    private final AtomicInteger counter = new AtomicInteger(0);

    public int incrementAndGet() {
        return counter.incrementAndGet();
    }

    public int get() {
        return counter.get();
    }

    public void reset() {
        counter.set(0);
    }
}
