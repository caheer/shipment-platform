package com.example.shipment.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class ExecutorConfig {

    @Value("${app.config.threads}")
    private int threads;

    @Bean(destroyMethod = "shutdown")
    public ExecutorService pdfExecutor() {
        return Executors.newFixedThreadPool(threads);
    }
}
