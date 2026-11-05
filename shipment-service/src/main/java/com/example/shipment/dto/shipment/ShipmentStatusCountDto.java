package com.example.shipment.dto.shipment;

import com.example.shipment.model.ShipmentStatus;

public record ShipmentStatusCountDto(
        ShipmentStatus status,
        long count
) {
}
