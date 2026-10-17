package com.example.invoicing.dto;

import com.example.invoicing.model.InvoiceStatus;
import com.example.invoicing.model.InvoiceType;
import com.example.shipment.model.Customer;
import com.example.shipment.model.Shipment;

import java.math.BigDecimal;
import java.time.LocalDateTime;

// I imagine this will be for loading one particular invoice when opened from the list
public record InvoiceDetailedDto(
        String invoiceNumber,
        Shipment shipment,
        Customer customer,
        String billingAddress,
        BigDecimal ratePerKm,
        int kilometersSpent,
        BigDecimal taxes,
        BigDecimal additionalCharges,
        BigDecimal totalAmount,
        InvoiceStatus invoiceStatus,
        InvoiceType invoiceType,
        LocalDateTime issuedAt,
        String issuedBy
) {
}
