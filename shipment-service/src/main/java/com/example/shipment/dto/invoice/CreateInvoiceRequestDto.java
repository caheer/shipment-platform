package com.example.shipment.dto.invoice;

import com.example.shipment.model.ShippingType;

import java.util.UUID;

public record CreateInvoiceRequestDto(
        UUID shipmentId,
        String shipmentName,
        String shipmentDescription,
        ShippingType shippingType,
        UUID customerId,
        String customerName,
        String customerEmail,
        UUID shipmentInvoiceRevisionId
) {}