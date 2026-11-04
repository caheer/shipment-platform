package com.example.shipment.model;

import com.example.shipment.model.comment.ShipmentComment;
import jakarta.persistence.*;
import jakarta.validation.constraints.Digits;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
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

    public Shipment(UUID shipmentId, String shipmentName, ShipmentStatus shipmentStatus, ShippingType shippingType,
                    String shipmentDescription, Customer customer, Location currentLocation,
                    Location destinationLocation, Courier assignedCourier, BigDecimal weightKg, BigDecimal declaredValue, ShipmentInsurance shipmentInsurance, LocalDateTime shipmentDateCreated) {
        this.shipmentId = shipmentId;
        this.shipmentName = shipmentName;
        this.shipmentStatus = shipmentStatus;
        this.shippingType = shippingType;
        this.shipmentDescription = shipmentDescription;
        this.customer = customer;
        this.currentLocation = currentLocation;
        this.destinationLocation = destinationLocation;
        this.assignedCourier = assignedCourier;
        this.weightKg = weightKg;
        this.declaredValue = declaredValue;
        this.shipmentInsurance = shipmentInsurance;
        this.shipmentDateCreated = shipmentDateCreated;
    }
    // name , description, weight, declared value - one method  for changing those, no event
    // customer - added/changed - event
    // type, destination - no changing
    // current location-  added/changed - event
    // shipment status - changed - event
    // assigned courier - added/changed - event
    // insurance - added/changd - event
    @Id
    @Column(name = "shipment_id")
    private UUID shipmentId;

    @Column(name = "shipment_name", nullable = false)
    private String shipmentName;

    @Enumerated(EnumType.STRING)
    @Column(name = "shipment_status", nullable = false, length = 50)
    private ShipmentStatus shipmentStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "shipping_type", nullable = false)
    private ShippingType shippingType;

    @Column(name = "shipment_description", length = 1000)
    private String shipmentDescription;

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
    private ShipmentInsurance shipmentInsurance;

    @Column(name = "shipment_date_created", nullable = false)
    private LocalDateTime shipmentDateCreated;

    @OneToMany(mappedBy = "shipment")
    private List<ShipmentComment> shipmentComments = new ArrayList<>();

    // Invoice revisions are managed by shipment-service; invoice entities live in invoice-service.
    @OneToMany(mappedBy = "shipment")
    private List<ShipmentInvoiceRevision> invoiceRevisions = new ArrayList<>();


    @Version
    @Column(name = "version")
    private Long version;

    public String getSummary() {
        return getShipmentName() + getShipmentDescription();
    }
}