package com.example.shipment.model;

import jakarta.persistence.*;

import java.util.Set;

@Entity
@Table(name = "insurance_providers")
public class InsuranceProvider {

    protected InsuranceProvider() {
    }

    // add appropriate constructor if there is a need to add Insurance providers within the app

    @Id
    @Column(name = "provider_code")
    private Long providerCode;

    @Column(name = "provider_name", length = 255, unique = true, nullable = false)
    private String providerName;

    @OneToMany(mappedBy = "provider")
    private Set<ShipmentInsurance> insurances;
}
