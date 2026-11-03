package com.example.shipment.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "shipment_invoice_revisions",
        uniqueConstraints = @UniqueConstraint(
                name = "uq_shipment_invoice_revision_number",
                columnNames = {"shipment_id", "revision_number"}))
@Getter
@Setter
public class ShipmentInvoiceRevision {

    protected ShipmentInvoiceRevision() {
    }

    public ShipmentInvoiceRevision(UUID revisionId, Shipment shipment, int revisionNumber) {
        this.shipmentInvoiceRevisionId = revisionId;
        this.shipment = shipment;
        this.revisionNumber = revisionNumber;
        this.creationStatus = InvoiceCreationStatus.PENDING;
        this.createdAt = LocalDateTime.now();
    }

    @Id
    @Column(name = "shipment_invoice_revision_id", nullable = false)
    private UUID shipmentInvoiceRevisionId;

    // Local JPA foreign key: shipment and revision are in the SAME database.
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "shipment_id", nullable = false)
    private Shipment shipment;

    @Column(name = "revision_number", nullable = false)
    private int revisionNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "creation_status", nullable = false, length = 30)
    private InvoiceCreationStatus creationStatus;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
}
