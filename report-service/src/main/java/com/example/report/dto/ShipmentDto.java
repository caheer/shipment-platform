package com.example.report.dto;

import java.util.UUID;

public record ShipmentDto(
        UUID shipmentId,
        String shipmentName,
        String shipmentStatus,
        String shipmentDescription
) {
}