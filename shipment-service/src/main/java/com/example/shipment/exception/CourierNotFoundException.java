package com.example.shipment.exception;

import java.util.UUID;

public class CourierNotFoundException extends RuntimeException {
    public CourierNotFoundException(UUID courierId) {
        super("Courier not found: " + courierId);
    }
}