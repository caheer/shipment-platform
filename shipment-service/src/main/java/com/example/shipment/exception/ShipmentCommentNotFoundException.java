package com.example.shipment.exception;

import java.util.UUID;

public class ShipmentCommentNotFoundException extends RuntimeException {
    public ShipmentCommentNotFoundException(UUID shipmentId) {
        super("Shipment not found: " + shipmentId);
    }
}
