package com.example.invoicing.repository;

import com.example.invoicing.model.InvoiceStatus;
import com.example.invoicing.model.InvoiceType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InvoiceSearchCriteria {
    private String invoiceNumber;
    private String shipmentName;
    private String customerName;
    private InvoiceStatus invoiceStatus;
    private InvoiceType invoiceType;
}
