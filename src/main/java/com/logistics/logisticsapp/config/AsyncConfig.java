package com.logistics.logisticsapp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

@Configuration
public class AsyncConfig {

    @Bean(name = "vehicleExecutor")
    public ExecutorService vehicleExecutor() {

        ThreadFactory factory = new ThreadFactory() {
            private final AtomicInteger counter = new AtomicInteger(1);

            @Override
            public Thread newThread(Runnable r) {
                Thread thread = new Thread(r);
                thread.setName("vehicle-exec-" + counter.getAndIncrement());
                thread.setDaemon(true);
                return thread;
            }
        };

        return Executors.newFixedThreadPool(50, factory);
    }
}