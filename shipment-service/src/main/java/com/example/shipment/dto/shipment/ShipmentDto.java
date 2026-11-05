package com.example.shipment.dto.shipment;

import java.util.UUID;

public record ShipmentDto(
        UUID shipmentId,
        String shipmentName
) {
}