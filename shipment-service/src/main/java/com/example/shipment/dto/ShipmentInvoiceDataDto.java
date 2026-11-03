package com.example.shipment.dto;

import com.example.shipment.model.ShippingType;

import java.util.UUID;

public record ShipmentInvoiceDataDto(
        UUID shipmentId,
        String shipmentName,
        String shipmentDescription,
        ShippingType shippingType,
        UUID customerId,
        String customerName,
        String customerEmail,
        String destinationLocation
) {}