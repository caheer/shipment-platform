package com.example.invoicing.model;


import com.example.shipment.model.Customer;
import com.example.shipment.model.Shipment;
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

    protected Invoice(){};

    @Id
    @Column(name = "invoice_id")
    private UUID invoiceId;

    @Column(name = "invoice_number", unique = true)
    private String invoiceNumber;

    @ManyToOne
    @JoinColumn(name = "shipment_id")
    private Shipment shipment;

    @JoinColumn(name = "customer_id")
    @ManyToOne
    private Customer customer;

    @Column(name = "billing_address")
    private String billingAddress;

    @Column(name = "rate_per_km", precision = 12, scale = 2)
    @Digits(integer = 12, fraction = 2)
    private BigDecimal ratePerKm;

    @Column(name = "kilometers_spent")
    private int kilometersSpent;

    @Column(name = "taxes", precision = 12, scale = 2)
    @Digits(integer = 12, fraction = 2)
    private BigDecimal taxes;

    @Column(name = "additional_charges", precision = 12, scale = 2)
    @Digits(integer = 12, fraction = 2)
    private BigDecimal additionalCharges;

    @Column(name = "total_amount", precision = 12, scale = 2)
    @Digits(integer = 12, fraction = 2)
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
