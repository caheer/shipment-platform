package com.example.shipment.model;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ShipmentTest {

    @Test
    void shouldReturnShipmentSummary() {

        // Arrange
        Shipment shipment = new Shipment(UUID.randomUUID());
        shipment.setShipmentName("Laptop");
        shipment.setShipmentDescription("Handle with care");

        // Act
        String result = shipment.getSummary();

        // Assert
        assertEquals("LaptopHandle with care", result);
    }
}