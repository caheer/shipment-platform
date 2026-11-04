package com.example.shipment.dto;

import com.example.shipment.model.ShipmentStatus;
import jakarta.validation.constraints.NotNull;

public record ShipmentStatusUpdateDto(
        @NotNull ShipmentStatus shipmentStatus
) {}