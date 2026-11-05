package com.example.shipment.dto.shipment;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record ShipmentCustomerUpdateDto(
        @NotNull UUID customerId
) {}