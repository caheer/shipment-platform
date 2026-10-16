package com.example.shipment.dto;

import com.example.shipment.model.ShipmentStatus;

import java.util.UUID;

public record ShipmentDetailsDto(
        UUID shipmentId,
        String shipmentName,
        ShipmentStatus shipmentStatus,
        String shipmentDescription
) {}