package com.example.invoicing.dto;

import com.example.invoicing.model.InvoiceStatus;
import com.example.invoicing.model.InvoiceType;

public record InvoiceSearchItemDto(
        String invoiceNumber,
        String shipmentName,
        String customerName,
        InvoiceStatus invoiceStatus,
        InvoiceType invoiceType) {
}
