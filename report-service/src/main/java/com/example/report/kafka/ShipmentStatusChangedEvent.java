package com.example.report.kafka;

import java.time.LocalDateTime;
import java.util.UUID;

public record ShipmentStatusChangedEvent(
        UUID shipmentId,
        String newStatus,
        LocalDateTime changedAt
) {
}