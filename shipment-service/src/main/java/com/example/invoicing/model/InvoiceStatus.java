package com.example.invoicing.model;

public enum InvoiceStatus {
    DRAFT("Draft"),
    ISSUED("Issued");

    private final String label;

    InvoiceStatus(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
