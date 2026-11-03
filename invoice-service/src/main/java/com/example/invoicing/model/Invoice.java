package com.example.invoicing.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Digits;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "invoices")
@Getter
@Setter
public class Invoice {

    public Invoice(UUID invoiceId) {
        this.invoiceId = invoiceId;
    }

    protected Invoice() {
    }

    @Id
    @Column(name = "invoice_id")
    private UUID invoiceId;

    // Logical reference to shipment_db; unique per invoice creation operation.
    // No cross-database foreign key or @ManyToOne mapping.
    @Column(name = "shipment_invoice_revision_id", nullable = false, unique = true)
    private UUID shipmentInvoiceRevisionId;

    @Column(name = "revision_number", nullable = false)
    private int revisionNumber;

    @Column(name = "shipment_id", nullable = false)
    private UUID shipmentId;

    @Column(name = "customer_id")
    private UUID customerId;

    @Column(name = "customer_name", nullable = false, length = 255)
    private String customerName;

    @Column(name = "customer_email", nullable = false, length = 255)
    private String customerEmail;

    @Column(name = "invoice_number", unique = true)
    private String invoiceNumber;

    @Column(name = "shipment_name", nullable = false)
    private String shipmentName;

    @Enumerated(EnumType.STRING)
    @Column(name = "shipping_type", nullable = false)
    private ShippingType shippingType;

    @Column(name = "billing_address")
    private String billingAddress;

    @Column(name = "rate_per_km", precision = 12, scale = 2)
    @Digits(integer = 10, fraction = 2)
    private BigDecimal ratePerKm;

    @Column(name = "kilometers_spent")
    private int kilometersSpent;

    @Column(name = "taxes", precision = 12, scale = 2)
    @Digits(integer = 10, fraction = 2)
    private BigDecimal taxes;

    @Column(name = "additional_charges", precision = 12, scale = 2)
    @Digits(integer = 10, fraction = 2)
    private BigDecimal additionalCharges;

    @Column(name = "total_amount", precision = 12, scale = 2)
    @Digits(integer = 10, fraction = 2)
    private BigDecimal totalAmount;

    @Column(name = "invoice_status")
    @Enumerated(EnumType.STRING)
    private InvoiceStatus invoiceStatus;

    @Column(name = "invoice_type")
    @Enumerated(EnumType.STRING)
    private InvoiceType invoiceType;

    @Column(name = "issued_at")
    private LocalDateTime issuedAt;

    @Column(name = "issued_by")
    private String issuedBy;

    @Version
    @Column(name = "version")
    private Long version;
}
