package com.example.shipment.dto;

import com.example.shipment.model.ShipmentStatus;

public record ShipmentStatusCountDto(
        ShipmentStatus status,
        long count
) {
}
