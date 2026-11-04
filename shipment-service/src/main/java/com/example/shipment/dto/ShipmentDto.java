package com.example.shipment.dto;

import java.util.UUID;

public record ShipmentDto(
        UUID shipmentId,
        String shipmentName
) {
}