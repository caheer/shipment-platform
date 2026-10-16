package com.example.shipment.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Digits;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "shipment_insurance")
public class ShipmentInsurance {

    @Id
    @Column(name = "shipment_insurance_id")
    private UUID shipmentInsuranceId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shipment_id", nullable = false)
    private Shipment shipment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "provider_code", nullable = false)
    private InsuranceProvider provider;

    @Digits(integer = 10, fraction = 2)
    @Column(name = "insured_value", nullable = false, precision = 12, scale = 2)
    private BigDecimal insuredValue;
}
