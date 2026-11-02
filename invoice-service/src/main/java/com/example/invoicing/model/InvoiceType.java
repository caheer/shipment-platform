package com.example.invoicing.model;

public enum InvoiceType {
    NORMAL("Standard Invoice"),
    PROFORMA("Proforma Invoice");

    private final String label;

    InvoiceType(String label) {
        this.label = label;
    }


    public String getLabel() {
        return label;
    }
}
