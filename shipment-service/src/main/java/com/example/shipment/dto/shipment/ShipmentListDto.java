package com.example.shipment.dto.shipment;

import com.example.shipment.model.ShipmentStatus;

import java.util.UUID;

public record ShipmentListDto(
        UUID shipmentId,
        UUID customerId,
        String customerName,
        String shipmentName,
        ShipmentStatus shipmentStatus
) {}