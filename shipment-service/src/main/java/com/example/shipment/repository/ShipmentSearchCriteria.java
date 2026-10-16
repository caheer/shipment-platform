package com.example.shipment.repository;

import com.example.shipment.model.ShipmentStatus;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
public class ShipmentSearchCriteria {

    private ShipmentStatus status;
    private UUID customerId;
    private UUID courierId;
    private BigDecimal weightKg;
    private BigDecimal declaredValue;
    private String shipmentName;
}