package com.example.shipment.dto;

import java.util.List;
import java.util.UUID;

public record ShipmentPdfDataDto(
        UUID shipmentId,
        String shipmentName,
        String shipmentDescription,
        String status,
        String shippingType,
        String customerName,
        String customerEmail,
        String currentLocation,
        String destinationLocation,
        List<ShipmentCommentDto> shipmentComments
) {}