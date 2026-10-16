package com.example.shipment.model;

public enum ShipmentStatus {
    CREATED("Created"),
    PICKED_UP("Picked Up"),
    IN_TRANSIT("In Transit"),
    ARRIVED_AT_FACILITY("Arrived At Facility"),
    IN_DELIVERY("In Delivery"),
    DELIVERED("Delivered");

    private final String label;

    ShipmentStatus(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}


