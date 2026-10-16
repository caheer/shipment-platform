package com.example.shipment.kafka;

import com.example.shipment.model.ShipmentStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record ShipmentStatusChangedEvent(
        UUID shipmentId,
        ShipmentStatus newStatus,
        LocalDateTime changedAt
) {
}