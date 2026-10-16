package com.example.shipment.exception;

import java.util.UUID;

public class ShipmentNotFoundException extends RuntimeException  {
    public ShipmentNotFoundException(UUID shipmentId) {
        super("Shipment not found: " + shipmentId);
    }
}
