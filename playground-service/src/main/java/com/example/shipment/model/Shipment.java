package com.example.shipment.model;

import com.example.shipment.model.comment.ShipmentComment;
import jakarta.persistence.*;
import jakarta.validation.constraints.Digits;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.*;

@Entity
@Table(name = "shipments")
@Getter
@Setter
public class Shipment {

    protected Shipment() {
    }

    public Shipment(UUID shipmentId) {
        this.shipmentId = shipmentId;
    }

    public Shipment(UUID shipmentId, Customer customer, ShipmentStatus shipmentStatus,
                    Location currentLocation, Location destinationLocation, String shipmentName,
                    String shipmentDescription, BigDecimal weightKg, BigDecimal declaredValue) {
        this.shipmentId = shipmentId;
        this.customer = customer;
        this.shipmentStatus = shipmentStatus;
        this.currentLocation = currentLocation;
        this.destinationLocation = destinationLocation;
        this.shipmentName = shipmentName;
        this.shipmentDescription = shipmentDescription;
        this.weightKg = weightKg;
        this.declaredValue = declaredValue;
    }

    @Id
    @Column(name = "shipment_id")
    private UUID shipmentId;

    @Enumerated(EnumType.STRING)
    @Column(name = "shipment_status", nullable = false, length = 50)
    private ShipmentStatus shipmentStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @JoinColumn(name = "current_location_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Location currentLocation;

    @JoinColumn(name = "destination_location_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Location destinationLocation;

    @JoinColumn(name = "assigned_courier_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Courier assignedCourier;

    @Column(name = "shipment_name", nullable = false)
    private String shipmentName;

    @Column(name = "shipment_description", length = 1000)
    private String shipmentDescription;

    @Digits(integer = 10, fraction = 3)
    @Column(name = "weight_kg", precision = 10, scale = 3)
    private BigDecimal weightKg;

    @Digits(integer = 12, fraction = 2)
    @Column(name = "declared_value", precision = 12, scale = 2)
    private BigDecimal declaredValue;

    // ShipmentTag is modeled as an entity with its own surrogate primary key
/*    @OneToMany(mappedBy = "shipment")
    private Set<ShipmentTag> shipmentTags = new HashSet<>();*/

    // optional
    @OneToOne(mappedBy = "shipment")
    private ShipmentInsurance insurance;

    @OneToMany(mappedBy = "shipment")
    private List<ShipmentComment>  shipmentComments = new ArrayList<>();

    public String getSummary() {
        return getShipmentName() + getShipmentDescription();
    }
}