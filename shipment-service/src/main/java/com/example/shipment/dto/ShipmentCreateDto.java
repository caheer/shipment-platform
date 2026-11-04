package com.example.shipment.dto;

import com.example.shipment.model.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public record ShipmentCreateDto(

        @NotBlank
        String shipmentName,

        @NotNull
        ShippingType shippingType,

        String shipmentDescription,
        UUID customerId,
        UUID currentLocationId,
        UUID destinationLocationId,
        UUID assignedCourierId,

        @DecimalMin("0.001")
        BigDecimal weightKg,

        @DecimalMin("0.00")
        BigDecimal declaredValue

) {}